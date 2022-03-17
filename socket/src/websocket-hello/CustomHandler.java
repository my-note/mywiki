package com.ws.demo;

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

import java.time.LocalDateTime;

/**
 * 创建自定义的助手类
 * 接受、处理、响应客户端websocket请求的核心业务处理类,frame是消息载体
 */
public class CustomHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    /**
     * 用于记录和管理所有客户端的channel
     */
    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        //获取客户端传过来的消息
        String content = msg.text();
        System.out.println("===================: " + content);

        //群发：

        for (Channel channel : clients) {
            //发给客户端的内容
            String reply = "服务器在【" + LocalDateTime.now() + "】接收到消息，消息为：" + content;

            channel.writeAndFlush(new TextWebSocketFrame(reply));
        }




        //下面这行代码也是群发，类似与上面的for循环
        //clients.writeAndFlush()




        /*
            //获取channel
            Channel channel = ctx.channel();


            if (msg instanceof HttpRequest){

                //显示客户端的远程地址
                System.out.println("channel.remoteAddress() = " + channel.remoteAddress());
                //定义发送的消息
                ByteBuf byteBuf = Unpooled.copiedBuffer("Hello netty", CharsetUtil.UTF_8);
                //构建一个http response
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                //添加响应头，数据类型和长度
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());

                //发送到客户端
                ctx.writeAndFlush(response);
        }*/
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
        System.out.println("捕获到异常");
        super.exceptionCaught(ctx, cause);
    }

    /**
     * 当客户端连接服务端之后（打开连接）
     * 获取客户端的channel,并且放到ChannelGroup管理
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("助手类添加");
        clients.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //System.out.println("助手类移除，例如浏览器关闭");
        //当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel,所以下面的可以注释
        //clients.remove(ctx.channel());
        System.out.println("移除客户端断开，客户端对应的长id为：" + ctx.channel().id().asLongText());
        System.out.println("移除客户端断开，客户端对应的短id为：" + ctx.channel().id().asShortText());
    }
}
