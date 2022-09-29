package com.hellozj.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static com.hellozj.common.config.DateSerializerConfig.DATE_TIME_FORMAT;

/**
 * 文件名: GsonUtil.java
 * 作者: zhuxiang
 * 时间: 2020/6/19 9:56
 * 描述: Gson工具类
 */
public class GsonUtil {
    /**
     * 不用创建对象,直接使用Gson.就可以调用方法
     */
    private static Gson gson = null;

    static {
        if (gson == null) {
            //当使用GsonBuilder方式时属性为空的时候输出来的json字符串是有键值key的,显示形式是"key":null，而直接new出来的就没有"key":null的
            gson = new GsonBuilder().setDateFormat(DATE_TIME_FORMAT).create();
        }
    }

    public GsonUtil() {
    }

    /**
     * 方法名: jsonToString
     * 作者/时间: zhuxiang-2020/6/19
     * 描述: 将object对象转成json字符串
     * 参数: obj
     * 返回: java.lang.String
     * 异常场景:
     */
    public static String jsonToString(Object obj) {
        String str = null;
        if (gson != null) {
            str = gson.toJson(obj);
        }
        return str;
    }

    /**
     * 方法名: jsonToBean
     * 作者/时间: zhuxiang-2020/6/19
     * 描述: 将json转成特定的cls的对象
     * 参数: str
     * 参数: cls
     * 返回: T
     * 异常场景:
     */
    public static <T> T jsonToBean(String str, Class<T> cls) {
        T t = null;
        if (gson != null) {
            //传入json对象和对象类型,将json转成对象
            t = gson.fromJson(str, cls);
        }
        return t;
    }

    /**
     * 方法名: jsonToList
     * 作者/时间: zhuxiang-2020/6/19
     * 描述: json字符串转成list
     * 参数: str
     * 返回: java.util.List<T>
     * 异常场景:
     */
    public static <T> List<T> jsonToList(String str) {
        List<T> list = null;
        if (gson != null) {
            list = gson.fromJson(str, new TypeToken<List<T>>() {
            }.getType());
        }
        return list;
    }

    /**
     * 方法名: jsonToList
     * 作者/时间: zhuxiang-2020/6/19
     * 描述: json字符串转成list
     * 参数: str
     * 返回: java.util.List<T>
     * 异常场景:
     */
    public static <T> List<T> jsonToList(String str, Class cla) {
        Type type = new ParameterizedTypeImpl(cla);
        List<T> list = null;
        if (gson != null) {
            list = gson.fromJson(str, type);
        }
        return list;
    }

    /**
     * 方法名: jsonToListMaps
     * 作者/时间: zhuxiang-2020/6/19
     * 描述: 转成list中有map的
     * 参数: gsonString
     * 返回: java.util.List<java.util.Map<java.lang.String,T>>
     * 异常场景:
     */
    public static <T> List<Map<String, T>> jsonToListMaps(String gsonString) {
        List<Map<String, T>> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString,
                    new TypeToken<List<Map<String, T>>>() {
                    }.getType());
        }
        return list;
    }

    /**
     * 方法名: jsonToMaps
     * 作者/时间: zhuxiang-2020/6/19
     * 描述: json字符串转成map的
     * 参数: gsonString
     * 返回: java.util.Map<java.lang.String,T>
     * 异常场景:
     */
    public static <T> Map<String, T> jsonToMaps(String gsonString) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }

    /**
     * 方法名: beanToJson
     * 作者/时间: zhuxiang-2020/6/19
     * 描述: 把一个bean（或者其他的字符串什么的）转成json
     * 参数: object
     * 返回: java.lang.String
     * 异常场景:
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

}