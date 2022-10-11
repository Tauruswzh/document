package com.demo.netty.service;

import com.demo.netty.entity.dto.RedisMessage;
import io.netty.channel.Channel;

import java.util.List;

/**
 * websocket服务
 */
public interface WebSocketService {

    /**
     * 订阅topic
     *
     * @param channelId 通道ID
     * @param userId    用户ID
     * @param topics    主题
     */
    void registerTopics(String channelId, Long userId, List<String> topics);

    /**
     * 移除topic中的 channelId
     *
     * @param channelId 通道ID
     * @param topics    主题
     */
    void removeTopics(String channelId, List<String> topics);

    /**
     * 注册活跃channel
     *
     * @param channel 通道
     */
    void addClient(Channel channel);

    /**
     * 往topic发送消息
     *
     * @param message 信息体
     */
    void sendMessage(RedisMessage message);

    /**
     * 获取当前在线用户数
     *
     * @return int
     */
    int clientNumber();
}
