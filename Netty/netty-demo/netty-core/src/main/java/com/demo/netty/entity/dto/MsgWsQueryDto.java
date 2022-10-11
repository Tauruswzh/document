package com.demo.netty.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * TABLE:msg_ws_receiver
 *
 * @mbg.generated websocket消息查询传输实体
 */
@Getter
@Setter
public class MsgWsQueryDto implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 消息主题，区分消息
     */
    private String topic;

    /**
     * 已读标识: 0 已读, 1 未读
     */
    private Integer isRead;

}