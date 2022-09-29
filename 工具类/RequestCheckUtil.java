package com.xx.order.util;

import com.alibaba.fastjson.JSON;
import com.xx.redis.constant.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

@Slf4j
@Component
public class RequestCheckUtil {

    @Autowired
    private RedisTemplate redisTemplate;
    private static RedisTemplate redisTemplateStatic;

    @PostConstruct
    public void init() {
        redisTemplateStatic = redisTemplate;
    }


    /**
     * 文件名: RequestCheckUtil.java
     * 作者: xiahao
     * 时间: 2021/5/26 上午9:50
     * 描述: 校验幂等
     * @param userId 用户ID
     * @param methodName 方法名
     * @param uniqueKey 业务唯一键
     * @param obj 请求的参数
     * @param excludeKeys 请求参数里面排除哪些字段
     */
    public static boolean checkIdempotency(String userId, String methodName, String uniqueKey, Object obj, String... excludeKeys) {
        Assert.notNull(userId,"param : 'userId' is null");
        Assert.notNull(methodName,"param : 'methodName' is null");
        Assert.notNull(obj,"param : 'obj' is null");

        String dedupParamMD5 = dedupParamMD5(obj, excludeKeys);
        String KEY = RedisConstant.METHOD_CHECK_PREFIX + userId + methodName + uniqueKey + dedupParamMD5;
        log.info("校验方法幂等 Key:{}",KEY);

        //存放redis
        long expireTime =  300; // 过期 300s内的重复请求会认为重复
        long expireAt = System.currentTimeMillis() + expireTime;
        String val = "expireAt@" + expireAt;
        // NOTE:直接SETNX不支持带过期时间，所以设置+过期不是原子操作，极端情况下可能设置了就不过期了，
        // 后面相同请求可能会误以为需要去重，所以这里使用底层API，保证SETNX+过期时间是原子操作
        Boolean firstSet = (Boolean) redisTemplateStatic.execute((RedisCallback<Boolean>) connection ->
                connection.set(KEY.getBytes(), val.getBytes(), Expiration.seconds(expireTime),
                        RedisStringCommands.SetOption.SET_IF_ABSENT));

        //false 不重复
        //true 重复
        return firstSet == null || !firstSet;
    }



    public static String dedupParamMD5(Object obj, String... excludeKeys) {
        //String类型直接去加密
        if (obj instanceof String){
            String md5deDupParam = HttpClientUtil.encodeByMD5(obj.toString());
            log.debug("md5deDupParam = {}, excludeKeys = {} {}", md5deDupParam, Arrays.deepToString(excludeKeys), obj.toString());
            return md5deDupParam;
        }

        //对象需要转换
        Map<String, String> map = HttpClientUtil.ObjectToMap(obj);
        log.info("校验方法幂等 原始map:{}",map);
        map.remove("serialVersionUID");
        //排除不需要的元素
        if (excludeKeys != null) {
            List<String> dedupExcludeKeys = Arrays.asList(excludeKeys);
            if (!dedupExcludeKeys.isEmpty()) {
                for (String dedupExcludeKey : dedupExcludeKeys) {
                    map.remove(dedupExcludeKey);
                }
            }
        }

        log.info("校验方法幂等 排除后map:{}",map);
        SortedMap<String,String> sortedMap = new TreeMap<>(map);
        //所有参与传参的参数按照accsii排序（升序）
        Set<Map.Entry<String, String>> paramTreeMap = sortedMap.entrySet();

        String paramTreeMapJSON = JSON.toJSONString(paramTreeMap);
        String md5deDupParam = HttpClientUtil.encodeByMD5(paramTreeMapJSON);
        log.debug("md5deDupParam = {}, excludeKeys = {} {}", md5deDupParam, Arrays.deepToString(excludeKeys), paramTreeMapJSON);
        return md5deDupParam;
    }
}