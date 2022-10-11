package com.demo.netty.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


/**
 * Gson工具类
 *
 * @author zhuxiang
 * @date 2020/11/26 0:37
 */
public class GsonUtil {
    /**
     * 不用创建对象,直接使用Gson.就可以调用方法
     */
    private static Gson gson = null;

    /**
     * 默认日期时间格式
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 默认日期格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 默认时间格式
     */
    public static final String TIME_FORMAT = "HH:mm:ss";

    static {
        if (gson == null) {
            //当使用GsonBuilder方式时属性为空的时候输出来的json字符串是有键值key的,显示形式是"key":null，而直接new出来的就没有"key":null的
            gson = new GsonBuilder()
                    // 设置时间格式
                    .setDateFormat(DATE_TIME_FORMAT)
                    // java8时间序列化和反序列化
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                    .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                    .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                    .registerTypeAdapter(LocalTime.class, new LocalTimeSerializer())
                    .registerTypeAdapter(LocalTime.class, new LocalTimeDeserializer())
                    .create();
        }
    }

    public GsonUtil() {
    }

    /**
     * 将object对象转成json字符串
     *
     * @param obj 对象
     * @return java.lang.String
     */
    public static String jsonToString(Object obj) {
        String str = null;
        if (gson != null) {
            str = gson.toJson(obj);
        }
        return str;
    }

    /**
     * 将json转成特定的cls的对象
     *
     * @param str   json字符串
     * @param clazz class
     * @return T
     */
    public static <T> T jsonToBean(String str, Class<T> clazz) {
        T t = null;
        if (gson != null) {
            //传入json对象和对象类型,将json转成对象
            t = gson.fromJson(str, clazz);
        }
        return t;
    }

    /**
     * json字符串转成list
     *
     * @param str json字符串
     * @return java.util.List<T>
     */
//    public static <T> List<T> jsonToList(String str) {
//        List<T> list = null;
//        if (gson != null) {
//            list = gson.fromJson(str, new com.google.common.reflect.TypeToken<List<T>>() {
//            }.getType());
//        }
//        return list;
//    }

    /**
     * json字符串转成list
     *
     * @param str   json字符串
     * @param clazz class
     * @return java.util.List<T>
     */
    public static <T> List<T> jsonToList(String str, Class clazz) {
        Type type = new ParameterizedTypeImpl(clazz);
        List<T> list = null;
        if (gson != null) {
            list = gson.fromJson(str, type);
        }
        return list;
    }

    /**
     * 转成list中有map的
     *
     * @param str json字符串
     * @return java.util.List<java.util.Map < java.lang.String, T>>
     */
//    public static <T> List<Map<String, T>> jsonToListMaps(String str) {
//        List<Map<String, T>> list = null;
//        if (gson != null) {
//            list = gson.fromJson(str,
//                    new com.google.common.reflect.TypeToken<List<Map<String, T>>>() {
//                    }.getType());
//        }
//        return list;
//    }

    /**
     * json字符串转成map的
     *
     * @param str json字符串
     * @return java.util.Map<java.lang.String, T>
     */
    public static <T> Map<String, T> jsonToMaps(String str) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(str, new com.google.gson.reflect.TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }

    /**
     * 把一个bean（或者其他的字符串什么的）转成json
     *
     * @param object 对象
     * @return java.lang.String
     */
    public static String beanToJson(Object object) {
        return gson.toJson(object);
    }

    private static class ParameterizedTypeImpl implements ParameterizedType {
        Class clazz;

        public ParameterizedTypeImpl(Class clz) {
            clazz = clz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

    private static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(localDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        }
    }

    private static class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            String datetime = json.getAsJsonPrimitive().getAsString();
            return LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        }
    }

    private static class LocalDateSerializer implements JsonSerializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(localDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        }
    }

    private static class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            String datetime = json.getAsJsonPrimitive().getAsString();
            return LocalDate.parse(datetime, DateTimeFormatter.ofPattern(DATE_FORMAT));
        }
    }

    private static class LocalTimeSerializer implements JsonSerializer<LocalTime> {
        @Override
        public JsonElement serialize(LocalTime localTime, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(localTime.format(DateTimeFormatter.ofPattern(TIME_FORMAT)));
        }
    }

    private static class LocalTimeDeserializer implements JsonDeserializer<LocalTime> {
        @Override
        public LocalTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            String datetime = json.getAsJsonPrimitive().getAsString();
            return LocalTime.parse(datetime, DateTimeFormatter.ofPattern(TIME_FORMAT));
        }
    }
}