package com.hellozj.third.util;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 通过MD5加密
 */
@Slf4j
public class MD5Util {

    public static String encodeByMD5(String str) {
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
}
