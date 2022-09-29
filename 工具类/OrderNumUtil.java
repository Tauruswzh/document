package com.hellozj.order.util;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.hellozj.common.enums.ExceptionEnum;
import com.hellozj.common.exception.HelloZjException;
import com.hellozj.xcloud.redis.XcloudRedisTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 文件名: OrderNumUtil.java
 * 作者: xiahao
 * 时间: 2020/6/9 18:09
 * 描述: 生成订单号
 */
@Component
@Slf4j
public class OrderNumUtil {
    @Autowired
    private XcloudRedisTemplate xRedisTemplate;
    private static XcloudRedisTemplate redisTemplate;

    @NacosValue("${order.num.env}")
    private String orderEvn;
    private static String staticOrderEvn;

    //被@PostConstruct修饰的方法会在服务器加载Servle的时候运行，并且只会被服务器执行一次。
    // PostConstruct在构造函数之后执行,init()方法之前执行。（PreDestroy（）方法在destroy()方法执行执行之后执行）
    @PostConstruct
    public void init() {
        redisTemplate = xRedisTemplate;
        staticOrderEvn = orderEvn;
    }

    /**
     * 方法名:  getOrderNum
     * 作者/时间: baozhiming-2020/5/24
     * 描述: 生成订单号
     * 参数: flag 0:非标 1:标准
     * 返回:
     * 异常场景:
     */
    public static String getOrderNum(String orderprex,int flag) {
        String orderId = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String current = dateFormat.format(new Date());
        String orderKey = "hellozj:orderNum:" + current + ":";

        //region 加锁
    /*    // 加锁处理
        String lockOrderKey = ELockKey.LOCK_ORDER_ID.value();
        String lockId = RedisLockUtil.lockFailThrowException(redisTemplate, lockOrderKey, 10, 1);
        try {
            // 生成订单号 ORDERPREX + yyyyMMdd + ######
            orderId = ORDERPREX + current + getUniqueness(orderKey);
        } finally {
            // 释放锁
            RedisLockUtil.unlock(redisTemplate, lockOrderKey, lockId);
        }
        if (StringUtils.isBlank(orderId)) {
            throw new HelloZjException(ExceptionEnum.CREATE_ORDERNUM_FAIL);
        }
        return orderId;*/
        //endregion
        try {
            // 生成订单号 ORDERPREX + yyyyMMdd + ######
            orderId = orderprex + flag +current + getUniqueness(orderKey);
            if (StringUtils.isNotBlank(staticOrderEvn) && !"prod".equals(staticOrderEvn)){
                orderId = "Test" + orderId;
            }
        }catch (Exception e){
            log.info("生成订单编号异常:{}", ExceptionUtils.getStackTrace(e));
            throw new HelloZjException(ExceptionEnum.CREATE_ORDERNUM_FAIL);
        }
        if (StringUtils.isBlank(orderId)) {
            throw new HelloZjException(ExceptionEnum.CREATE_ORDERNUM_FAIL);
        }
        return orderId;
    }

//    private static String getUniqueness(String key) {
//        StringBuilder sb=new StringBuilder();
//        Long increment = redisTemplate.opsForValue().increment(key,1);
////        redisTemplate.expire(key, 86400,TimeUnit.SECONDS);
//        String  result = String.format("%06d", increment); //不足补齐
//        sb.append(result);
//        return sb.toString();
//    }

    private static Date getTodayEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    private static String getUniqueness(String key) {
        StringBuilder sb=new StringBuilder();
        RedisAtomicLong counter = new RedisAtomicLong( key, redisTemplate.getConnectionFactory());
        counter.expireAt(getTodayEndTime());
        long increment = counter.incrementAndGet();
        String  result = String.format("%06d", increment);
        sb.append(result);
        return sb.toString();
    }

}
