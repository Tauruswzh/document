Long类型返回前端丢失精度


解决：将Long转换成String

###1.主键式
//@JSONField(serializeUsing=ToStringSerializer.class)
//@JsonSerialize(using = ToStringSerializer.class)
@JsonFormat(shape = JsonFormat.Shape.STRING)
private Long zkShopId;

@JsonFormat(shape = JsonFormat.Shape.STRING)作用就是将JSON数据的此字段格式化为字符串类型，保证前端超过17位不会出现精度丢失问题！
由于JavaScript中Number类型的自身原因，并不能完全表示Long型的数字，在Long长度大于17位时会出现精度丢失的问题。
所以，不应该使用ResponseBean<Long>，应该使用ResponseBean<String>，转换成字符串类型的。


###2.配置类
####2.1.fastJson
```java
package com.cj.storehouse.config;
 
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
 
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
 
/**
 * @program: storehouse-parent
 * @description: 注册拦截器
 * @author: Adorable Sir
 * @create: 2022-01-25 17:20
 **/
@Configuration
public class WebAppConfig implements WebMvcConfigurer {
 
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        FastJsonHttpMessageConverter fastConvert = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        /**
         * 解决精度丢失问题
         */
        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
        serializeConfig.put(Long.class, ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
        fastJsonConfig.setSerializeConfig(serializeConfig);
 
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        fastConvert.setSupportedMediaTypes(fastMediaTypes);
        fastConvert.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters((HttpMessageConverter<?>) fastConvert);
    }
}
```
####2.2.jackSon
```java
package jnpf.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Configuration
@Order(-1)
@Slf4j
public class JsonMessageConverter {

    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        //通过该方法对mapper对象进行设置，所有序列化的对象都将按改规则进行系列化。
        objectMapper.configure(Feature.IGNORE_UNKNOWN, true);
        objectMapper.configure(Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        //jackson中自定义处理序列化和反序列化
        SimpleModule module = new SimpleModule();
        //Method for adding serializer to handle values of specific type.
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        module.addSerializer(long.class, ToStringSerializer.instance);
        //register the module with the object-mapper
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
```