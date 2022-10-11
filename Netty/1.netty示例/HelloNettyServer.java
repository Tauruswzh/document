package com.example.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HelloNettyServer {
    public static void main(String[] args) throws Exception {
        //创建一组线程池
        //主线程池：用于接受客户端的请求链接，不做任何处理
        NioEventLoopGroup master = new NioEventLoopGroup();
        //从线程池：主线程池会把任务交给他，处理任务
        NioEventLoopGroup slave = new NioEventLoopGroup();

        try {
            //创建服务器启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //设置主从线程池
            serverBootstrap.group(master,slave)
                    //添加NIO双向通道
                    .channel(NioServerSocketChannel.class)
                    //添加子处理器，用于处理从线程池的任务
                    .childHandler(new HelloNettyServerInitializer());

            //启动服务，并设置端口，设置为异步
            ChannelFuture channelFuture = serverBootstrap.bind(7770).sync();
            //监听关闭的channel，设置为异步
            channelFuture.channel().closeFuture().sync();
        }finally {
            //关闭线程池
            master.shutdownGracefully();
            slave.shutdownGracefully();
        }
    }
}
