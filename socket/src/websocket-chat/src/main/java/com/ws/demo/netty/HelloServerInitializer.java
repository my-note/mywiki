package com.ws.demo.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 初始化器，channel注册后，会执行里面的初始化方法
 */
public class HelloServerInitializer extends ChannelInitializer<SocketChannel> {
    /**
     * workgroup-channel-助手类（拦截器）
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //通过管道添加助手类
        ChannelPipeline pipeline = ch.pipeline();
        //request、response编解码
        pipeline.addLast(new HttpServerCodec());
        //对写大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());
        //对httpMessage进行聚合，集合成FullHttpRequest FullHttpResponse
        pipeline.addLast(new HttpObjectAggregator(1024 * 64));

        //==========================以上是对http协议支持



        // ====================== 增加心跳支持 start    ======================
        // 针对客户端，如果在指定时间内没有向服务端发送读写心跳(ALL)，则主动断开 单位秒
        // 如果是读空闲或者写空闲，不处理
        // 客户端需要定时发送心跳包哦
        pipeline.addLast(new IdleStateHandler(8, 10, 12));
        // 自定义的空闲状态检测
        pipeline.addLast(new HeartBeatHandler());
        // ====================== 增加心跳支持 end    ======================





        //websocket服务器处理的协议，用于指定给客户端连接访问的路由：/ws
        //会帮你处理握手动作：handshaking(close,ping,pong) ping + pong = 心跳
        //对于websocket来讲，都是以frames进行传输的，不同的数据类型对于的frame也不同
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        //添加自定义的助手类，返回"hello netty"
        pipeline.addLast(new CustomHandler());
    }
}
