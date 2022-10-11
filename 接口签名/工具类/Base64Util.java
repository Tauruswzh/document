package com.hellozj.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 文件名: Base64Util.java
 * 作者: zhuxiang
 * 时间: 2020/11/30 16:26
 * 描述: Base64  加解密
 */
public class Base64Util {


    /**
     * 方法名: encode
     * 作者/时间: zhuxiang-2020/11/30
     * 描述: 请在此处输入方法描述信息
     * 参数: 加密
     * 返回: java.lang.String
     * 异常场景:
     */
    public static String encode(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 方法名: decode
     * 作者/时间: zhuxiang-2020/11/30
     * 描述: 解密
     * 参数: str
     * 返回: java.lang.String
     * 异常场景:
     */
    public static String decode(String str) {
        byte[] decode = Base64.getDecoder().decode(str);
        return new String(decode, StandardCharsets.UTF_8);
    }

    /**
     * 方法名: decode
     * 作者/时间: zhuxiang-2020/11/30
     * 描述: 解密
     * 参数: str
     * 返回: java.lang.String
     * 异常场景:
     */
    public static String decode(byte[] str) {
        byte[] decode = Base64.getDecoder().decode(str);
        return new String(decode, StandardCharsets.UTF_8);
    }
}
