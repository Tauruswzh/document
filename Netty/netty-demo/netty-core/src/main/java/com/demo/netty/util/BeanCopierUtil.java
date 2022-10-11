package com.demo.netty.util;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

/**
 * BeanCopier工具类
 */
public class BeanCopierUtil {
    /**
     * BeanCopier的缓存
     */
    static final ConcurrentHashMap<String, BeanCopier> BEAN_COPIER_CACHE = new ConcurrentHashMap<>();

    /**
     * BeanCopier的copy
     *
     * @param source 源文件的
     * @param target 目标文件
     */
    public static void copy(Object source, Object target) {
        Assert.notNull(source, "The copied original cannot be empty");
        Assert.notNull(target, "The copy target file cannot be empty");
        String key = genKey(source.getClass(), target.getClass());
        BeanCopier beanCopier;
        if (BEAN_COPIER_CACHE.containsKey(key)) {
            beanCopier = BEAN_COPIER_CACHE.get(key);
        } else {
            beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
            BEAN_COPIER_CACHE.put(key, beanCopier);
        }
        beanCopier.copy(source, target, null);
    }

    /**
     * 生成key
     *
     * @param srcClazz 源文件的class
     * @param tgtClazz 目标文件的class
     * @return string
     */
    private static String genKey(Class<?> srcClazz, Class<?> tgtClazz) {
        return srcClazz.getName() + tgtClazz.getName();
    }
}