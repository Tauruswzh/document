package com.demo.netty.entity.dao;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * TABLE:msg_ws_content
 *
 * websocket消息内容实体
 * <p>
 * DATE: 2021-01-27 17:08
 */
@Getter
@Setter
public class MsgWsContent implements Serializable {
    /**
     * 主键ID
     * msg_ws_content.id
     */
    private Long id;

    /**
     * 发送者ID
     * msg_ws_content.sender
     */
    private Long sender;

    /**
     * 主题
     * msg_ws_content.topic
     */
    private String topic;

    /**
     * 消息类型: 1 文本; 2 图片; 3 链接; 4 图文
     * msg_ws_content.type
     */
    private Integer type;

    /**
     * 消息标题
     * msg_ws_content.title
     */
    private String title;

    /**
     * 消息内容
     * msg_ws_content.content
     */
    private String content;

    /**
     * 扩展信息,JSON格式
     * msg_ws_content.additional
     */
    private String additional;

    /**
     * 创建时间
     * msg_ws_content.create_time
     */
    private Date createTime;

}