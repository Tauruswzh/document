package com.demo.netty.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * TABLE:msg_ws_receiver
 *
 * @mbg.generated websocket消息接收者传输实体
 */
@Getter
@Setter
public class MsgWsReceiverDto implements Serializable {
    /**
     * 主键ID
     * msg_ws_receiver.id
     */
    @NotNull(message = "主键ID不能为空")
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
    @NotNull(message = "已读标识不能为空")
    private Integer isRead;

}