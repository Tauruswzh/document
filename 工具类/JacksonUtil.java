package com.xx.common.util;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xx.common.jackson.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

/**
 * 文件名: JacksonUtil.java
 * 作者: luoxiaoxiao
 * 时间: 2020/10/10 18:15
 * 描述: fastxml jackson转换工具类
 */
@Slf4j
public class JacksonUtil {

	private static final ObjectMapper OBJECT_MAPPER;

	static {
		OBJECT_MAPPER = new Jackson2ObjectMapperBuilder()
				.findModulesViaServiceLoader(true)
				.simpleDateFormat(DatePattern.NORM_DATETIME_PATTERN)
				.modules(new JavaTimeModule())
				.build();
	}

	public static String toJSONString(Object obj) {
		try {
			return OBJECT_MAPPER.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("json序列化异常", e);
		}
	}

	public static <T> T parseObject(String jsonStr, Class<T> clazz) {
		try {
			return OBJECT_MAPPER.readValue(jsonStr, clazz);
		} catch (IOException e) {
			throw new RuntimeException("json反序列化异常", e);
		}
	}

	public static <T> T parseObject(String jsonStr, TypeReference<T> tTypeReference) {
		try {
			return OBJECT_MAPPER.readValue(jsonStr, tTypeReference);
		} catch (IOException e) {
			throw new RuntimeException("json反序列化异常", e);
		}
	}
}
