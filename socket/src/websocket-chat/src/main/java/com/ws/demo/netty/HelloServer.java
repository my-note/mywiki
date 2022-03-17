package com.ws.demo.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

/**
 * 实现客户端发送一个请求，服务器返回hello netty
 */

@Component
public class HelloServer {


    /**
     * 内部类方式的单例
     */
    private static class SingletionWSServer {
        private static final HelloServer INSTANCE = new HelloServer();
    }

    public static HelloServer getInstance() {
        return SingletionWSServer.INSTANCE;
    }

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private ChannelFuture channelFuture;

    private HelloServer() {

        //定义一对线程组

        // 主线程组用于接受客户端的连接，但是不作任何处理，和老板一样，不做事情
        this.bossGroup = new NioEventLoopGroup();
        // 从线程组，老板线程组会把任务丢给它，让手下线程组去做任务
        this.workerGroup = new NioEventLoopGroup();
        //netty服务器的创建，启动类
        this.serverBootstrap = new ServerBootstrap();

        //设置主从线程组
        serverBootstrap.group(bossGroup, workerGroup)
                //设置nio的双向通道
                .channel(NioServerSocketChannel.class)
                //字处理器，用于处理workerGroup
                .childHandler(new HelloServerInitializer());

    }

    public void start() {

        //启动server, bind
        final int port = 8088;
        this.channelFuture = serverBootstrap.bind(port);
        System.out.println("netty websocket server 启动完毕,p: " + port);

    }


}
