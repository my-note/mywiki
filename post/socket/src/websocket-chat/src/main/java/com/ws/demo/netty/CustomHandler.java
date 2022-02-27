package com.ws.demo.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ws.demo.service.ChatMsgService;
import com.ws.demo.utils.JsonUtils;
import com.ws.demo.utils.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建自定义的助手类
 * 接受、处理、响应客户端websocket请求的核心业务处理类,frame是消息载体
 */
public class CustomHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 用于记录和管理所有客户端的channel
     */
    public static final ChannelGroup USERS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        //1. 获取客户端传过来的消息
        String content = msg.text();
        System.out.println("===================: " + content);
        DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
        Channel currentChannel = ctx.channel();
        //2. 判断消息类型，根据不同的类型来处理不同的业务
        Integer action = dataContent.getAction();

        if (MsgActionEnum.CONNECT.type.equals(action)) {
            // 	2.1  当websocket 第一次open的时候，初始化channel，把用的channel和userid关联起来
            String senderId = dataContent.getChatMsg().getSenderId();
            UserChannelRel.put(senderId, currentChannel);

            // 测试
            for (Channel c : USERS) {
                System.out.println(c.id().asLongText());
            }
            UserChannelRel.output();
        } else if (MsgActionEnum.CHAT.type.equals(action)) {
            //  2.2  聊天类型的消息，把聊天记录保存到数据库，同时标记消息的签收状态[未签收]
            ChatMsg chatMsg = dataContent.getChatMsg();
            String msgText = chatMsg.getMsg();
            String receiverId = chatMsg.getReceiverId();
            String senderId = chatMsg.getSenderId();

            // 保存消息到数据库，并且标记为 未签收
            ChatMsgService chatMsgService = (ChatMsgService) SpringUtil.getBean("chatMsgService");
            String msgId = chatMsgService.saveMsg(chatMsg);

            chatMsg.setMsgId(msgId);

            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setChatMsg(chatMsg);

            // 发送消息
            // 从全局用户Channel关系中获取接受方的channel
            Channel receiverChannel = UserChannelRel.get(receiverId);
            if (receiverChannel == null) {
                // TODO channel为空代表用户离线，推送消息（JPush，个推，小米推送）
            } else {
                // 当receiverChannel不为空的时候，从ChannelGroup去查找对应的channel是否存在
                Channel findChannel = USERS.find(receiverChannel.id());
                if (findChannel != null) {
                    // 用户在线
                    receiverChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContentMsg)));
                } else {
                    // 用户离线 TODO 推送消息
                }
            }
        } else if (MsgActionEnum.SIGNED.type.equals(action)) {
            //  2.3  签收消息类型，针对具体的消息进行签收，修改数据库中对应消息的签收状态[已签收]

            // 扩展字段在signed类型的消息中，代表需要去签收的消息id，逗号间隔
            String msgIdsStr = dataContent.getExtand();
            String[] msgIds = msgIdsStr.split(",");

            List<String> msgIdList = new ArrayList<>();
            for (String mid : msgIds) {
                if (StringUtils.isNotBlank(mid)) {
                    msgIdList.add(mid);
                }
            }

            System.out.println(msgIdList.toString());

            if (!msgIdList.isEmpty()) {
                // 批量签收
                ChatMsgService chatMsgService = (ChatMsgService) SpringUtil.getBean("chatMsgService");
                chatMsgService.updateMsgSigned(msgIdList);
            }

        }else if (MsgActionEnum.KEEPALIVE.type.equals(action)) {
            //  2.4  心跳类型的消息
            System.out.println("收到来自channel为[" + currentChannel + "]的心跳包...");
        }

    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel...注册");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel...移除");
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel...活跃");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel...不活跃");
        super.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelId读取完毕...");
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("用户事件触发。。。");
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel可写可改");
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 发生异常之后关闭连接（关闭channel），随后从ChannelGroup中移除
        ctx.channel().close();
        USERS.remove(ctx.channel());
    }

    /**
     * 当客户端连接服务端之后（打开连接）
     * 获取客户端的channel,并且放到ChannelGroup管理
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        USERS.add(ctx.channel());
    }

    /**
     * 客户端断开连接，该事件会触发
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        System.out.println("移除客户端断开，客户端对应的长id为：" + ctx.channel().id().asLongText());
        System.out.println("移除客户端断开，客户端对应的短id为：" + ctx.channel().id().asShortText());
        String channelId = ctx.channel().id().asShortText();
        System.out.println("客户端被移除，channelId为：" + channelId);

        // 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
        USERS.remove(ctx.channel());
    }
}
