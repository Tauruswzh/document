package com.xx.order.util;


import com.google.common.collect.Lists;
import com.xx.common.enums.ExceptionEnum;
import com.xx.common.exception.XXException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
* 文件名: CheckSignUtil.java
* 描述: 校验签名
*/
@Slf4j
@Component
public class CheckSignUtil {
    /**
     * 验签
     * sign: 对方的签名
     * key: 加密的key
     * obj: 参数
     */
    public static void checkSign(String sign,String key,Object obj) {
        String signValue = encodeSign(obj, key);
        log.info("验签 signValue: "+signValue);
        if (StringUtils.isBlank(signValue) || !sign.equals(signValue)){
            throw new XXException(ExceptionEnum.SIGN_ERROR);
        }
    }

    /**
     * sign 签名 （参数名按ASCII码从小到大排序（字典序）+key+MD5+转大写签名）
     */
    public static String encodeSign(Object obj,String key) {
        Assert.notNull(key,"签名 param : 'key' is null");
        Assert.notNull(obj,"签名 param : 'obj' is null");

        //转换
        Map<String, String> map = ObjectToMap(obj);
        log.info("sign 签名 map :"+map.toString());
        SortedMap<String,String> sortedMap=new TreeMap<String,String>(map);

        return paramEncodeSign(sortedMap,key);
    }

    /**
     * 对象转map
     */
    private static Map<String,String> ObjectToMap(Object obj){
        Map<String,String> param=new HashMap<>();
        try {
            Class<?> clazz=obj.getClass();
            for (Field field:clazz.getDeclaredFields()){
                field.setAccessible(true);
                String fieldName=field.getName();
                if (field.get(obj) != null){
                    String value = field.get(obj).toString();
                    param.put(fieldName,value);
                }
            }
            return param;
        }catch (Exception e){
            throw new RuntimeException(String.format("对象转换成MAP异常 obj:%s", obj.toString()));
        }
    }

    /**
     * sign 签名 （参数名按ASCII码从小到大排序（字典序）+key+MD5+转大写签名）
     */
    private static String paramEncodeSign(SortedMap<String,String> sortedMap, String key) {
        Assert.notNull(key,"签名 param : 'key' is null");
        Assert.notEmpty(sortedMap,"签名 param : 'sortedMap' is null");

        //所有参与传参的参数按照accsii排序（升序）
        Set<Map.Entry<String, String>> entries = sortedMap.entrySet();
        //遍历组装
        Iterator<Map.Entry<String, String>> iterator = entries.iterator();
        List<String> values = Lists.newArrayList();

        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            String k = String.valueOf(entry.getKey());
            String v = String.valueOf(entry.getValue());
            if (StringUtils.isNotEmpty(v) && entry.getValue() !=null && !"sign".equals(k) && !"serialVersionUID".equals(k) && !"key".equals(k)) {
                values.add(k + "=" + v);
            }
        }
        //key放最后
        values.add("key="+key);
        String sign = StringUtils.join(values, "&");
        log.info("sign 签名 sign: "+sign);
        return encodeByMD5(sign);
    }

    /**
     * 通过MD5加密
     */
    private static String encodeByMD5(String str) {
        log.info("MD5加密 str: "+str);
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(str.getBytes("UTF-8"));
            return String.format("%032x", new BigInteger(1, bytes));
        } catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).", nsae);
        } catch (UnsupportedEncodingException uee) {
            throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).", uee);
        }
    }


    public static void main(String[] args) {
        //验签
        CheckSignUtil.checkSign(dto.getSign(),dto.getKey(),dto);
    }
}
