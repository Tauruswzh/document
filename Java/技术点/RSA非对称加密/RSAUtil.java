package witpdp.utils;

import org.apache.commons.codec.binary.Base64;

/**
 * 描述：RSA工具类，用于密钥生成
 */
public class RSAUtil {
    /**
     * 方法名：genKeyPair
     * 描述：生成一对密钥
     * fileKey:文件key
     **/
    public static void genKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024,new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        String privateKeyStr = new String(Base64.encodeBase64(privateKey.getEncoded()));
        String publicKeyStr = new String(Base64.encodeBase64(publicKey.getEncoded()));
        //System.out.println("私钥:"+privateKeyStr);
        //System.out.println("公钥:"+publicKeyStr);
    }

    public static void main(String[] args) {
/*        try {
            genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }*/
        //示例 加密字符串
        String str = "PDP-PDS2-G27C-3WTU-4T7F";
        //加密密文形成注册码
        String encryptStr = EncryptUtil.encryptSingle(str);
        System.out.println(encryptStr);

    }
}
