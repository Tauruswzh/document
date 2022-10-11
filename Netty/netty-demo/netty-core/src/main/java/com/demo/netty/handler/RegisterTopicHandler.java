package com.demo.netty.handler;

import com.demo.netty.config.NettyProperties;
import com.demo.netty.service.WebSocketService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 订阅topic
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class RegisterTopicHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final String TOPIC_KEY = "topic";

    private final NettyProperties nettyProperties;
    private final WebSocketService webSocketService;

    /**
     * 描述: 从通道中获取数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {
        log.info("RegisterTopicHandler..." + ctx.channel().id());
        // 获取用户ID
        final Long userId = ctx.channel().attr(AttributeKeyHolder.USER_ID_ATTRIBUTE_KEY).get();
        Assert.notNull(userId, "userId can not be null");

        // 获取订阅的主题
        String uri = httpRequest.uri();
        QueryStringDecoder query = new QueryStringDecoder(uri, true);
        final List<String> topics = query.parameters().get(TOPIC_KEY);

        log.info("user: {} ,topic: {}", userId, topics.toString());
        //信息存储到redis
        webSocketService.registerTopics(ctx.channel().id().asLongText(), userId, topics);

        //传递给WebSocketHandler
        ctx.channel().attr(AttributeKeyHolder.TOPIC_ATTRIBUTE_KEY).set(topics);

        //重置URL,否则进入WebSocketServerProtocolHandler,会连接失败
        httpRequest.setUri(nettyProperties.getUrl());

        //增加引用次数,将数据传入下一个channel中
        ctx.fireChannelRead(httpRequest.retain());
    }
}
