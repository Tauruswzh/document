package witpdp.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * 文件名：EncryptUtil
 * 描述：加密工具类
 */
public class EncryptUtil {
    //私钥，请妥善保存
    public static final String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKR75SsiJ0mIHohy5+KQ/XXTJRX3ljdQJ3G4QfYvw1+DcuY+Y6IYDejHE3qwXjgjgWhjzI81b80+yJRjCAqVxiVCqtbZBcH5odGpU4NeFtb7EZiaGPp+qdB6DMoS3M0UQ+02BJekCFw5KxKNhmXv6HWuPCGn8TJqUYbaWPbm5mmvAgMBAAECgYAmZlZGD58kZ4bZTBoepsLFcxnBWx/Sof/TaLTdiKEP91wnrIEOIpd8tJ0zk2Ersf5QJJxpAoyrWfDx03zZG8Y6JBgUTvLc+pYcmsDPUD+PsgOwOFhUCuuKxFxoPQrzwrOvh7yWXiOC+gcBriOQNi6TMF8/vQWyDwDmwMWoFvsyQQJBAOix3sHLQ2tEsOEQexXa8jU4bgq/LZrtoNvZkUP/+cvK/sxy19avHxHN/k48pDbRylPmD4ciuiJAlruajmvh6ocCQQC09SXAgOdof2imCbUSpLQ5Ph+VTFdgXLfinUr8/VbE5qfyXlAv+BuHtPXo3PWQJhtXWcAAuVYAq9jeN95DaYmZAkEAvNsP1e1ozHi1rhS4MSbACZ3BAgvjsahHM+fC1JO+Yz5fygMzVlRPPR4Y1dq11KA0PB+uXpk6xG3zaFOqX7i9pQJBAJfebnKjPJMs6sz+tKMS69Zm1ubcDTDoX8OmsvqkO6BA3I7CrJW1qpU4QHHQj58M/EFRJ5zz7sPddcL6LGVzO3ECQE4ARFruJCta8P0MgAeVrvuh0Mo2/9BmFi65g6wf/S9ZhluT5oYhwLFEWt+5jeaD6+f7y+O9g8PF2DrpPvqZUyU=";

    /**
     * 方法名：encryptSingle
     * 描述：加密单个字符串
     * 参数：[value]
     * fileKey:文件key
     **/
    public static String encryptSingle(String value) {
        String encryptStr = "";
        if (StringUtils.isNotBlank(value)) {
            try {
                encryptStr = encryptBASE64(encryptByPrivateKey(value,privateKey));
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return encryptStr;
    }

    /**
     * 方法名：encryptByPrivateKey
     * 描述：根据私钥加密单个字符串
     * 参数：[data, priKey]
     * fileKey:文件key
     **/
    public static byte[] encryptByPrivateKey(String data, String priKey) throws Exception {
        byte[] keyBytes = decryptBASE64(priKey);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(1, privateKey);
        return cipher.doFinal(data.getBytes("utf-8"));

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
     * 方法名：encryptBASE64
     * 描述：字节数组转成字符
     * 参数：[bytes]
     * fileKey:文件key
     * 返回：java.lang.String
     **/
    public static String encryptBASE64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }
}
