package com.demo.netty.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * netty参数配置
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "health.message.netty")
public class NettyProperties implements Serializable {

    private static final long serialVersionUID = 5210914393003529116L;

    /**
     * WS连接端口
     */
    private int port;

    /**
     * Boos EventLoopGroup 线程数量
     */
    private int bossThreads;

    /**
     * Worker EventLoopGroup 线程数量
     */
    private int workerThreads;

    /**
     * keepAlive 参数,测试链接的状态
     * 如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文
     */
    private boolean keepAlive;

    /**
     * 重复使用本地地址和端口
     */
    private boolean reuseAddr;

    /**
     * backlog 参数,指定了队列的大小
     */
    private int backlog;

    /**
     * WS连接地址
     */
    private String url;

    /**
     * 读超时时长,单位秒
     */
    private int readerIdle;

    /**
     * 写超时时长,单位秒
     */
    private int writerIdle;

    /**
     * 读/写超时时长,单位秒
     */
    private int allIdle;

}
