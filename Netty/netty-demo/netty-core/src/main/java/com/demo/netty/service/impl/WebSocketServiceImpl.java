package com.demo.netty.service.impl;

import com.demo.netty.entity.constant.RedisConstant;
import com.demo.netty.entity.dao.MsgWsContent;
import com.demo.netty.entity.dto.RedisMessage;
import com.demo.netty.service.MsgWsContentService;
import com.demo.netty.service.WebSocketService;
import com.demo.netty.util.GsonUtil;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * websocket服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketServiceImpl implements WebSocketService {

    /**
     * 用于记录所有的在线客户端,netty会自动移除无效的channel
     */
    private static final ChannelGroup CLIENT_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, RedisMessage> redisMessageRedisTemplate;

    /**
     * 订阅topic
     *
     * @param channelId 通道ID
     * @param userId    用户ID
     * @param topics    主题
     */
    @Override
    public void registerTopics(String channelId, Long userId, List<String> topics) {
        //<topic,channelId,mark>
        HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
        for (String topic : topics) {
            if (StringUtils.hasText(topic)) {
                log.info("registerTopics: topic:{},channelId:{}", topic, channelId);
                opsForHash.put(topic, channelId, userId.toString());
                stringRedisTemplate.expire(topic, 2, TimeUnit.DAYS);
            }
        }
    }

    /**
     * 移除topic中的 channelId
     *
     * @param channelId 通道ID
     * @param topics    主题
     */
    @Override
    public void removeTopics(String channelId, List<String> topics) {
        HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
        for (String topic : topics) {
            if (StringUtils.hasText(topic)) {
                log.info("removeTopics: topic:{},channelId:{}", topic, channelId);
                opsForHash.delete(topic, channelId);
            }
        }
    }

    /**
     * 注册活跃channel
     *
     * @param channel 通道
     */
    @Override
    public void addClient(Channel channel) {
        CLIENT_GROUP.add(channel);
    }

    /**
     * 往topic发送消息
     *
     * @param message 信息体
     */
    @Override
    public void sendMessage(RedisMessage message) {
        // 发送数据到redis的频道
        redisMessageRedisTemplate.convertAndSend(RedisConstant.REDIS_TOPIC_KEY, message);
    }

    /**
     * 获取当前在线用户数
     *
     * @return int
     */
    @Override
    public int clientNumber() {
        return CLIENT_GROUP.size();
    }


    /**
     * 订阅redis消息
     */
    @Component
    @RequiredArgsConstructor
    public static class RedisMessageReceiver {

        private final StringRedisTemplate stringRedisTemplate;
        private final MsgWsContentService msgWsContentService;

        public static final String LISTENER_METHOD = "receiveMessage";

        /**
         * 订阅
         */
        public void receiveMessage(RedisMessage redisMessage) {
            log.info("订阅的redisMessage: {}", GsonUtil.beanToJson(redisMessage));
            final String topic = redisMessage.getTopic();
            final Long msgId = redisMessage.getMsgId();
            final Long receiver = redisMessage.getReceiver();
            MsgWsContent wsContent = msgWsContentService.selectByPrimaryKey(msgId);

            HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
            Map<String, String> channelMap = opsForHash.entries(topic);
            for (Channel channel : CLIENT_GROUP) {
                if (Objects.equals(channelMap.get(channel.id().asLongText()), receiver.toString())) {
                    //响应客户端
                    channel.writeAndFlush(new TextWebSocketFrame(GsonUtil.beanToJson(wsContent)));
                }
            }
        }
    }

}
