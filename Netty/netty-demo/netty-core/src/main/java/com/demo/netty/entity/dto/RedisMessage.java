package com.demo.netty.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * redis消息实体
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedisMessage implements Serializable {

    /**
     * 发送者Id
     */
    private Long sender;

    /**
     * 接收者ID
     */
    private Long receiver;

    /**
     * 订阅主题
     */
    private String topic;

    /**
     * 消息ID
     */
    private Long msgId;

    /**
     * 动作
     */
    private Integer action;
}
