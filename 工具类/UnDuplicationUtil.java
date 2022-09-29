package com.hellozj.order.util;

import com.hellozj.common.constants.RedisConstant;
import com.hellozj.common.enums.ExceptionEnum;
import com.hellozj.common.exception.HelloZjException;
import com.hellozj.xcloud.redis.XcloudRedisTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
* 文件名: UnDuplicationClient.java
* 作者: xiahao
* 时间: 2020/5/14 9:58
* 描述: 生成token令牌,存储到redis,防止页面重读提交
*/
@Component
@Slf4j
public class UnDuplicationUtil {

    @Autowired
    private XcloudRedisTemplate xcloudRedisTemplate;
    private static XcloudRedisTemplate redisTemplate;

    //被@PostConstruct修饰的方法会在服务器加载Servle的时候运行，并且只会被服务器执行一次。
    // PostConstruct在构造函数之后执行,init()方法之前执行。（PreDestroy（）方法在destroy()方法执行执行之后执行）
    @PostConstruct
    public void init() {
        redisTemplate = xcloudRedisTemplate;
    }

    /**
    * 方法名:  getToken
    * 作者/时间: xiahao-2020/5/14
    * 描述: 生成token
    * 参数: null
    * 返回: token
    * 异常场景:
    */
    public static String getToken(Long num){
        //1; 生成随机码
        String token = UUID.randomUUID().toString().replace("-", "");
        //2: 存放到redis,过期时间10分钟
        redisTemplate.opsForValue().set(RedisConstant.ORDER_UN_DUPLICATION_PREFIX + num.toString(),token,10, TimeUnit.MINUTES);
        log.info("UnDuplicationClient 页面防重复提交 getToken, 生成的token: {},操作人id{}",token,num);
        return token;
    }


  /**
  * 方法名:  checkToken
  * 作者/时间: xiahao-2020/5/14
  * 描述: 校验token
  * 参数: token  (前端传递的token)
  * 返回: boolean
  * 异常场景:
  */
  public static  boolean checkToken(String token,Long num){
      Assert.notNull(token,"param : 'token' not null");

        //1; 根据key获取到token 未获取到: 过期
        String redisToken = (String) redisTemplate.opsForValue().get(RedisConstant.ORDER_UN_DUPLICATION_PREFIX + num.toString());
        if (StringUtils.isBlank(redisToken)){
            throw new HelloZjException(ExceptionEnum.TIMEOUT_CREATE_ORDER);
        }
        //2: 比较 不相等则无效
       log.info("UnDuplicationClient 页面防重复提交 checkToken, 根据key获取到的token: {},操作人id{}",redisToken,num);
        if (StringUtils.equals(token,redisToken)){//验证有效返回false
            //2: 刷新token 或者删除
            getToken(num);
//            redisTemplate.delete(UN_DUPLICATION+num.toString());
            return false;
        }
        return true;//验证无效返回true
    }
}
