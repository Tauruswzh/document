package com.demo.netty.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * webSocket消息传输实体
 */
@Setter
@Getter
public class WsMsgVo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 发送者ID
     */
    private Long sender;

    /**
     * 接收者ID
     */
    private Long receivers;

    /**
     * 消息主题，区分消息
     */
    private String topic;

    /**
     * 消息类型: 1 文本; 2 图片; 3 链接; 4 图文
     */
    private Integer type;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息详情
     */
    private String content;

    /**
     * 扩展信息,JSON格式
     */
    private String additional;

    /**
     * 已读标识: 0 已读, 1 未读
     */
    private Integer isRead;
}
