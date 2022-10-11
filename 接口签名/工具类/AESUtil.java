package com.hellozj.common.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;

/**
 * 文件名: AESUtil.java
 * 作者: luoxiaoxiao
 * 时间: 2020/8/25 14:15
 * 描述: AES加解密工具
 */
public class AESUtil {

    //加密方式
    private static final String ALGORITHM_KEY = "AES";
    //数据填充方式
    private static final String ALGORITHM_DESC = "AES/CBC/PKCS7Padding";//"算法/模式/补码方式"
    //避免重复new生成多个BouncyCastleProvider对象，因为GC回收不了，会造成内存溢出
    //只在第一次调用decrypt()方法时才new 对象
    public static boolean initialized = false;

    /*
     * 加密
     */
    public static byte[] encrypt(byte[] originalContent, byte[] encryptKey, byte[] ivByte) {
        initialize();
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_DESC);
            SecretKeySpec skeySpec = new SecretKeySpec(encryptKey, ALGORITHM_KEY);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(ivByte));
            return cipher.doFinal(originalContent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * AES解密
     * 填充模式AES/CBC/PKCS7Padding
     * 解密模式128
     */
    public static byte[] decrypt(byte[] content, byte[] aesKey, byte[] ivByte) {
        initialize();
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_DESC);
            Key sKeySpec = new SecretKeySpec(aesKey, ALGORITHM_KEY);
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, generateIV(ivByte));// 初始化
            return cipher.doFinal(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * BouncyCastle作为安全提供，防止我们加密解密时候因为jdk内置的不支持改模式运行报错。
     **/
    public static void initialize() {
        if (initialized)
            return;
        Security.addProvider(new BouncyCastleProvider());
        initialized = true;
    }

    // 生成iv
    public static AlgorithmParameters generateIV(byte[] iv) throws Exception {
        AlgorithmParameters params = AlgorithmParameters.getInstance(ALGORITHM_KEY);
        params.init(new IvParameterSpec(iv));
        return params;
    }
}
