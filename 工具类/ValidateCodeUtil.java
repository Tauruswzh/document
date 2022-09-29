package com.xx.common.util;


import com.xx.redis.util.RedisKeyUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

public class ValidateCodeUtil {

    /**
     * 方法名: validateSmsCode
     * 作者/时间: wangyang-2020/6/12
     * 描述: 请在此处输入类描述信息
     * 参数: smsKey
     * 参数: phone
     * 参数: code
     * 返回: 校验通过-返回tre 校验不通过返回false
     * 异常场景:
     */
    public static boolean  validateSmsCode(StringRedisTemplate redisTemplate, String smsKey, String phone, String smsCode,String module) {
        String strSmskey = RedisKeyUtil.SmsCodeKey(smsKey,phone);
        String strSmsAllowKey =  RedisKeyUtil.SmsAllowSendKey(phone,module);
        /** 只要校验过验证码，就允许再次发送 */
        redisTemplate.delete(strSmsAllowKey);
        Object objCode = redisTemplate.opsForValue().get(strSmskey);
        if (objCode != null) {
            if (smsCode.equals(objCode.toString())) {
                redisTemplate.delete(strSmskey);
                return true;
            }
        }
        return false;
    }
}
