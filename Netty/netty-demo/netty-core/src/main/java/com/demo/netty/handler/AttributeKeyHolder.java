package com.demo.netty.handler;

import io.netty.util.AttributeKey;

import java.util.List;

/**
 * 属性密钥
 */
public interface AttributeKeyHolder {

    AttributeKey<Long> USER_ID_ATTRIBUTE_KEY = AttributeKey.valueOf("userId");

    AttributeKey<List<String>> TOPIC_ATTRIBUTE_KEY = AttributeKey.valueOf("topics");
}
