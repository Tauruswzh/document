package com.demo.netty.entity.enums;

/**
 * webSocket动作枚举
 */
public enum WsActionEnum {

    /**
     * webSocket动作枚举
     */
    KEEPALIVE(0, "客户端保持心跳"),
    CONNECT(1, "第一次(或重连)初始化连接"),
    SIGNED(2, "消息签收"),
    NOTICE(3, "通知消息"),
    ;

    Integer type;
    String desc;

    WsActionEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer type() {
        return this.type;
    }

    public String desc() {
        return this.desc;
    }
}
