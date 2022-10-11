package com.example.netty.socket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
* 描述: 初始化器，channel注册之后，会执行里面的初始化方法
*/
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        //通过SocketChannel 获取对应的管道
        ChannelPipeline pipeline = channel.pipeline();

        //通过管道添加handler
        //HttpServerCodec 是由netty自己提供的助手类，可以理解为拦截器
        //当请求到服务器，我们需要解码，响应到客户端做编码
        pipeline.addLast(new HttpServerCodec());
        //在http上有一些数据流产生，有大有小，我们对其进行处理，需使用netty对下数据流写提供支持
        pipeline.addLast(new ChunkedWriteHandler());
        //对httpMessage 进行聚合处理，聚合成request或response
        pipeline.addLast(new HttpObjectAggregator(1024*64));

        //本handler 会帮你处理 一些繁重复杂的事情
        //能帮你处理握手动作：handshaking(close, ping, pong) ping+pong=心跳
        //对于websocket来讲，都是以frams进行传输的，不同的数据类型对于的frams也不同
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        //CustomHandler 添加自定义的助手类，可以获取信息并处理
        pipeline.addLast(new ChatHandler());
    }
}
