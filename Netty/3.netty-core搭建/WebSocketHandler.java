package com.demo.netty.handler;

import com.demo.netty.entity.dto.RedisMessage;
import com.demo.netty.service.WebSocketService;
import com.demo.netty.util.GsonUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * websocket 消息处理
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Autowired
    private WebSocketService webSocketService;

    /**
    * 描述: 从通道中获取数据
    */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info("接受信息: {}", ctx.channel().id());

        final Long userId = ctx.channel().attr(AttributeKeyHolder.USER_ID_ATTRIBUTE_KEY).get();
        final List<String> topics = ctx.channel().attr(AttributeKeyHolder.TOPIC_ATTRIBUTE_KEY).get();

        final RedisMessage msgText = GsonUtil.jsonToBean(msg.text(), RedisMessage.class);

        log.info("userId: {} ,topics: {},send: {}", userId, topics, GsonUtil.beanToJson(msgText));
        sendMessage(msgText, ctx);
    }

    /**
     * 描述: cannal 注册
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("注册活跃channel: {}", ctx.channel().id().asLongText());
        webSocketService.addClient(ctx.channel());
    }

    /**
     * 描述: cannal 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("通道异常: {},{}", cause.getMessage(), cause);
        final List<String> topics = ctx.channel().attr(AttributeKeyHolder.TOPIC_ATTRIBUTE_KEY).get();
        if (!CollectionUtils.isEmpty(topics)) {
            webSocketService.removeTopics(ctx.channel().id().asLongText(), topics);
        }
        ctx.close();
    }

    /**
     * 描述: cannal 活跃
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("新的客户端连接: {}", ctx.channel().id().asLongText());
    }

    /**
     * 描述: cannal 不活跃
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端断开连接: {}", ctx.channel().id().asLongText());
        final List<String> topics = ctx.channel().attr(AttributeKeyHolder.TOPIC_ATTRIBUTE_KEY).get();
        if (!CollectionUtils.isEmpty(topics)) {
            webSocketService.removeTopics(ctx.channel().id().asLongText(), topics);
        }
    }

    /**
     * 描述: cannal 移除
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端断开连接: {}", ctx.channel().id().asLongText());
        final List<String> topics = ctx.channel().attr(AttributeKeyHolder.TOPIC_ATTRIBUTE_KEY).get();
        if (!CollectionUtils.isEmpty(topics)) {
            webSocketService.removeTopics(ctx.channel().id().asLongText(), topics);
        }
    }

    /**
     * 描述: 发送信息
     */
    private void sendMessage(RedisMessage msgText, ChannelHandlerContext ctx) {
        switch (msgText.getAction()) {
            // 客户端保持心跳
            case 0:
                log.info("客户端心跳: {}", ctx.channel().id().asLongText());
                ctx.writeAndFlush(new TextWebSocketFrame("ok"));
                break;
            // 第一次(或重连)初始化连接
            case 1:
                log.info("新的客户端连接: {}", ctx.channel().id().asLongText());
                break;
            // 消息签收
            case 2:
                log.info("消息签收: {}", ctx.channel().id().asLongText());
                break;
            // 通知消息
            case 3:
                log.info("通知消息: {}", ctx.channel().id().asLongText());
                webSocketService.sendMessage(msgText);
                break;
            default:
                break;
        }
    }
}

