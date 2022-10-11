package com.demo.netty.entity.dao;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * TABLE:msg_ws_receiver
 *
 * websocket消息接收者实体
 */
@Getter
@Setter
public class MsgWsReceiver implements Serializable {
    /**
     * 主键ID
     * msg_ws_receiver.id
     */
    private Long id;

    /**
     * 内容ID
     * msg_ws_receiver.content_id
     */
    private Long contentId;

    /**
     * 接收者ID
     * msg_ws_receiver.receiver
     */
    private Long receiver;

    /**
     * 已读标识: 0 已读, 1 未读
     * msg_ws_receiver.is_read
     */
    private Integer isRead;

}