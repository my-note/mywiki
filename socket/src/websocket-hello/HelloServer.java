package com.ws.demo;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 实现客户端发送一个请求，服务器返回hello netty
 */
public class HelloServer {


    public static void main(String[] args) throws InterruptedException {

        //定义一对线程组

        // 主线程组用于接受客户端的连接，但是不作任何处理，和老板一样，不做事情
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 从线程组，老板线程组会把任务丢给它，让手下线程组去做任务
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //netty服务器的创建，启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            //设置主从线程组
            serverBootstrap.group(bossGroup, workerGroup)
                    //设置nio的双向通道
                    .channel(NioServerSocketChannel.class)
                    //字处理器，用于处理workerGroup
                    .childHandler(new HelloServerInitializer());

            System.out.println("服务端开启等待客户端连接。。。");

            //  启动server, bind8088,同步方式启动（等待8088起来完成）
            ChannelFuture channelFuture = serverBootstrap.bind(8088).sync();

            //监听关闭的channel,设置为同步方式
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }











}
