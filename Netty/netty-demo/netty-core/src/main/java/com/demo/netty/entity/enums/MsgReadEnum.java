package com.demo.netty.entity.enums;

/**
 * 消息阅读枚举
 */
public enum MsgReadEnum {

    // 0 已读, 1 未读
    READ(0, "已读"),
    UNREAD(1, "未读");

    Integer code;
    String message;

    MsgReadEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }
}
