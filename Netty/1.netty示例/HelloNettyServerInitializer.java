package com.example.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
* 描述: 初始化器，channel注册之后，会执行里面的初始化方法
*/
public class HelloNettyServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        //通过SocketChannel 获取对应的管道
        ChannelPipeline pipeline = channel.pipeline();

        //通过管道添加handler
        //HttpServerCodec 是由netty自己提供的助手类，可以理解为拦截器
        //当请求到服务器，我们需要解码，响应到客户端做编码
        pipeline.addLast("HttpServerCodec",new HttpServerCodec());
        //CustomHandler 添加自定义的助手类，可以获取信息并处理
        pipeline.addLast("CustomHandler",new CustomHandler());
    }
}
