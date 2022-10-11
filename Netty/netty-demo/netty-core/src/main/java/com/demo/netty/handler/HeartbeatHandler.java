package com.demo.netty.handler;

import com.demo.netty.service.WebSocketService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 心跳设置
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

    private final WebSocketService webSocketService;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
                // 读超时
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                // 写超时
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                // 读/写超时
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        log.info("---READER_IDLE---: {}", ctx.channel().id().asLongText());
    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        log.info("---WRITER_IDLE---: {}", ctx.channel().id().asLongText());
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
        log.info("---ALL_IDLE---: {}", ctx.channel().id().asLongText());
        //从redis中删除保存的通道ID
        List<String> topics = ctx.channel().attr(AttributeKeyHolder.TOPIC_ATTRIBUTE_KEY).get();
        webSocketService.removeTopics(ctx.channel().id().asLongText(), topics);
        log.info("移除不活跃channel: {}; topic: {}, 当前在线用户: {}", ctx.channel().id(), topics.toString(), webSocketService.clientNumber());
        ctx.channel().close();
    }
}
