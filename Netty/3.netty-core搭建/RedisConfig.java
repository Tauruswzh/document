package com.demo.netty.config;

import com.demo.netty.entity.constant.RedisConstant;
import com.demo.netty.entity.dto.RedisMessage;
import com.demo.netty.service.impl.WebSocketServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis配置
 */
@Configuration
public class RedisConfig {

    /**
     * 订阅redis, 发布/订阅
     *
     * @param connectionFactory redis连接工厂
     * @param listenerAdapter   消息监听
     * @return org.springframework.data.redis.listener.RedisMessageListenerContainer
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
                                                                       MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        //监听的topic
        container.addMessageListener(listenerAdapter, new PatternTopic(RedisConstant.REDIS_TOPIC_KEY));
        return container;
    }

    /**
     * 订阅Adapter  订阅类和订阅方法
     */
    @Bean
    public MessageListenerAdapter messageListenerAdapter(WebSocketServiceImpl.RedisMessageReceiver redisMessageReceiver) {
        MessageListenerAdapter adapter = new MessageListenerAdapter();
        adapter.setDefaultListenerMethod(WebSocketServiceImpl.RedisMessageReceiver.LISTENER_METHOD);
        adapter.setDelegate(redisMessageReceiver);
        adapter.setSerializer(new Jackson2JsonRedisSerializer<>(RedisMessage.class));
        adapter.afterPropertiesSet();
        return adapter;
    }

    /**
     * 订阅模版
     */
    @Bean
    public RedisTemplate<String, RedisMessage> redisMessageRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RedisMessage> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisMessage.class));
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
