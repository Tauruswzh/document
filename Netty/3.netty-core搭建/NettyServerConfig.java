package com.demo.netty.config;

import com.demo.netty.handler.AuthHandler;
import com.demo.netty.handler.HeartbeatHandler;
import com.demo.netty.handler.RegisterTopicHandler;
import com.demo.netty.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Netty服务配置
 * ContextRefreshedEvent：spring内置事件，当所有的bean都初始化完成并被成功装载后会触发该事件
 * DisposableBean：在容器销毁该bean的时候获得一次回调
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class NettyServerConfig implements ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    private final NettyProperties nettyProperties;
    private final WebSocketHandler webSocketHandler;
    private final AuthHandler authHandler;
    private final RegisterTopicHandler registerTopicHandler;
    private final HeartbeatHandler heartbeatHandler;

    private Channel serverChannel;

    /**
    * 描述: 主线程池 用于接受客户端的请求链接，不做任何处理
    */
    @Bean
    public EventLoopGroup boosGroup() {
        return new NioEventLoopGroup(nettyProperties.getBossThreads());
    }

    /**
     * 描述: 从线程池 主线程池会把任务交给它，让其做任务
     */
    @Bean
    public EventLoopGroup workerGroup() {
        return new NioEventLoopGroup(nettyProperties.getWorkerThreads());
    }

    /**
     * 描述: 配置启动类
     */
    @Bean
    public ServerBootstrap serverBootstrap() {
        //服务启动类
        ServerBootstrap bootstrap = new ServerBootstrap();
        //设置主从线程池
        bootstrap.group(boosGroup(), workerGroup())
                //设置nio双向通道
                .channel(NioServerSocketChannel.class)
                //添加子处理器，用于处理线程池的任务
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline()
                                // 心跳设置
                                .addLast(new IdleStateHandler(nettyProperties.getReaderIdle(),
                                        nettyProperties.getWriterIdle()
                                        , nettyProperties.getAllIdle()))
                                .addLast(heartbeatHandler)
                                //http upgrade ,所以需要http相关配置
                                .addLast(new HttpServerCodec())
                                .addLast(new HttpObjectAggregator(1024 * 64))
                                .addLast(new ChunkedWriteHandler())
                                //身份认证
                                .addLast(authHandler)
                                //订阅topic
                                .addLast(registerTopicHandler)
                                //如果被请求 HttpRequest的端点是"/websocket"，则处理该升级握手
                                .addLast(new WebSocketServerProtocolHandler(nettyProperties.getUrl()))
                                .addLast(webSocketHandler);

                    }
                })
                // backlog参数指定了队列的大小
                .option(ChannelOption.SO_BACKLOG, nettyProperties.getBacklog())
                // 测试链接的状态
                .childOption(ChannelOption.SO_KEEPALIVE, nettyProperties.isKeepAlive())
                // 重复使用本地地址和端口
                .childOption(ChannelOption.SO_REUSEADDR, nettyProperties.isReuseAddr());

        return bootstrap;
    }

    /**
     * 描述: 启动
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            //启动服务，设置端口号
            Channel channel = serverBootstrap().bind(nettyProperties.getPort()).sync().channel();
            this.serverChannel = channel;
            log.info("netty web socket started on port: " + nettyProperties.getPort());
        } catch (InterruptedException e) {
            log.error("netty Interrupted Exception: {}", ExceptionUtils.getStackTrace(e));
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 描述: 销毁
     */
    @Override
    public void destroy() {
        log.info("netty web socket stop on port: " + nettyProperties.getPort());
        if (serverChannel != null) {
            serverChannel.close();
        }
        boosGroup().shutdownGracefully();
        workerGroup().shutdownGracefully();
    }
}
