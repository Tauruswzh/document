package com.demo.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 身份认证
 */
@Slf4j
@Component
@ChannelHandler.Sharable  //解决多个客户端并发请求
public class AuthHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final String USER_ID = "userId";

    /**
     * 描述: 从通道中获取数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {
        log.info("AuthHandler..." + ctx.channel().id());

        // 获取用户ID
        String uri = httpRequest.uri();
        QueryStringDecoder query = new QueryStringDecoder(uri, true);
        final List<String> userIds = query.parameters().get(USER_ID);

        if (CollectionUtils.isEmpty(userIds)) {
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED);
            ctx.writeAndFlush(response);
            return;
        }
        Long userId = Long.parseLong(userIds.get(0));
        ctx.channel().attr(AttributeKeyHolder.USER_ID_ATTRIBUTE_KEY).set(userId);

        //表示传递消息至下一个处理器
        ctx.fireChannelRead(httpRequest.retain());
    }

}
