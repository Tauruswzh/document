package com.hellozj.third.util;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
* 文件名: RSAUtil.java
* 描述: RSA加密工具类
*/
public class RSAUtil {
    /** 公钥 */
    public static String publicKey;
    /** 私钥 */
    public static String privateKey;
    /** 指定加密算法为RSA */
    private static String ALGORITHM = "RSA";
    /** 字符集 */
    private static String charsetName = "UTF-8";
    /** 指定key的大小 */
    private static int KEYSIZE = 512;

    /**
    * 描述: 初始化生成公钥和私钥
    */
    public static void generateKey() {
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            SecureRandom secureRandom = new SecureRandom();//随机数生成器
            keyPairGenerator.initialize(KEYSIZE,secureRandom);//设置512位长的密钥
            KeyPair keyPair = keyPairGenerator.generateKeyPair();//开始创建
            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
            //base64转码
            publicKey = Base64.encodeBase64String(rsaPublicKey.getEncoded());
            privateKey = Base64.encodeBase64String(rsaPrivateKey.getEncoded());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 描述: 私钥匙加密或解密
     */
    public static String encryptByprivateKey(String content, String privateKeyStr, int opmode) {
        // 私钥要用PKCS8进行处理
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyStr));
        KeyFactory keyFactory;
        PrivateKey privateKey;
        Cipher cipher;
        byte[] result;
        String text = null;
        try {
            keyFactory = KeyFactory.getInstance(ALGORITHM);
            // 还原Key对象
            privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(opmode, privateKey);
            //加密
            if (opmode == Cipher.ENCRYPT_MODE) {
                result = cipher.doFinal(content.getBytes());
                text = Base64.encodeBase64String(result);
            //解密
            } else if (opmode == Cipher.DECRYPT_MODE) {
                result = cipher.doFinal(Base64.decodeBase64(content));
                text = new String(result, charsetName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }


    /**
     * 描述: 公钥匙加密或解密
     */
    public static String encryptByPublicKey(String content, String publicKeyStr, int opmode) {
        // 公钥要用X509进行处理
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyStr));
        KeyFactory keyFactory;
        PublicKey publicKey;
        Cipher cipher;
        byte[] result;
        String text = null;
        try {
            keyFactory = KeyFactory.getInstance(ALGORITHM);
            // 还原Key对象
            publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(opmode, publicKey);
            //加密
            if (opmode == Cipher.ENCRYPT_MODE) {
                result = cipher.doFinal(content.getBytes());
                text = Base64.encodeBase64String(result);
            //解密
            } else if (opmode == Cipher.DECRYPT_MODE) {
                result = cipher.doFinal(Base64.decodeBase64(content));
                text = new String(result, charsetName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    // 测试方法
    public static void main(String[] args) {
        /**
         * 注意： 私钥加密必须公钥解密 公钥加密必须私钥解密
         */
        System.out.println("-------------生成两对秘钥，分别发送方和接收方保管-------------");
        RSAUtil.generateKey();
        System.out.println("公钥匙给接收方:" + RSAUtil.publicKey);
        System.out.println("私钥给发送方:" + RSAUtil.privateKey);

        System.out.println("-------------第一个栗子，私钥加密公钥解密-------------");
        // String textsr = "早啊，你吃早饭了吗？O(∩_∩)O~";
        // // 私钥加密
        // String cipherText = RSAUtil.encryptByprivateKey(textsr,
        // RSAUtil.privateKey, Cipher.ENCRYPT_MODE);
        // System.out.println("发送方用私钥加密后：" + cipherText);
        // // 公钥解密
        // String text = RSAUtil.encryptByPublicKey(cipherText,
        // RSAUtil.publicKey, Cipher.DECRYPT_MODE);
        // System.out.println("接收方用公钥解密后：" + text);

        System.out.println("-------------第二个栗子，公钥加密私钥解密-------------");
        // 公钥加密
        String textsr = "吃过啦！你吃了吗？O(∩_∩)O~";

        String cipherText = RSAUtil.encryptByPublicKey(textsr, RSAUtil.publicKey, Cipher.ENCRYPT_MODE);
        System.out.println("接收方用公钥加密后：" + cipherText);
        // 私钥解密
        String text = RSAUtil.encryptByprivateKey(cipherText, RSAUtil.privateKey, Cipher.DECRYPT_MODE);
        System.out.print("发送方用私钥解密后：" + text);
    }

}
