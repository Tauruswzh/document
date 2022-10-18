package witpdp.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import witpdp.exception.ExceptionCode;
import witpdp.exception.PdpRuntimeException;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * 文件名：DecryptUtil
 * 描述：解密工具类
 */
public class DecryptUtil {
    //公钥
    public static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCke+UrIidJiB6IcufikP110yUV95Y3UCdxuEH2L8Nfg3LmPmOiGA3oxxN6sF44I4FoY8yPNW/NPsiUYwgKlcYlQqrW2QXB+aHRqVODXhbW+xGYmhj6fqnQegzKEtzNFEPtNgSXpAhcOSsSjYZl7+h1rjwhp/EyalGG2lj25uZprwIDAQAB";

    /**
     * 方法名：decryptSingle
     * 描述：解密单个字符串
     * 参数：[value]
     * fileKey:文件key
     * 返回：java.lang.String
     **/
    public static String decryptSingle(String value) {
        String str = "";
        if (StringUtils.isNotBlank(value)) {
            try {
                str = new String(decryptByPublicKey(decryptBASE64(value), publicKey));
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return str;
    }

    /**
     * 方法名：decryptByPublicKey
     * 描述：根据公钥解密字节数组
     * 参数：[data, pubKey]
     * fileKey:文件key
     * 返回：byte[]
     **/
    public static byte[] decryptByPublicKey(byte[] data, String pubKey) throws Exception{
        byte[] keyBytes = decryptBASE64(pubKey);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(2,publicKey);
        return cipher.doFinal(data);
    }


    /**
     * 方法名：decryptBASE64
     * 描述：解码转成字节数组
     * 参数：[key]
     * fileKey:文件key
     * 返回：byte[]
     **/
    public static byte[] decryptBASE64(String key) {
        return Base64.decodeBase64(key);
    }

    /**
     * 方法名：veriy
     * 描述：验证code与解密后的注册码是否相等
     * 参数：[code, regKey]
     * fileKey:文件key
     * 返回：void
     **/
    public static void veriy(String code, String regKey) {
        if (StringUtils.isBlank(code) || StringUtils.isBlank(regKey)) {
            throw new PdpRuntimeException(ExceptionCode.CORECONFIG_PARAM_NOT_NULL);
        }
        if (!code.equals(decryptSingle(regKey))) {
            throw new PdpRuntimeException(ExceptionCode.COMMON_REG_KEY_ERROR);
        }
    }
}
