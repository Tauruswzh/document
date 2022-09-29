package com.hellozj.order.util;

import com.hellozj.common.enums.ExceptionEnum;
import com.hellozj.common.exception.HelloZjException;
import com.hellozj.xcloud.redis.XcloudRedisTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
* 文件名: CountDistanceUtil.java
* 作者: xiahao
* 时间: 2020/6/23 19:05
* 描述: 使用redis计算距离
*/
@Component
@Slf4j
public class CountDistanceUtil {
    @Autowired
    private XcloudRedisTemplate xRedisTemplate;
    private static XcloudRedisTemplate redisTemplate;

    //被@PostConstruct修饰的方法会在服务器加载Servle的时候运行，并且只会被服务器执行一次。
    // PostConstruct在构造函数之后执行,init()方法之前执行。（PreDestroy（）方法在destroy()方法执行执行之后执行）
    @PostConstruct
    public void init() {
        redisTemplate = xRedisTemplate;
    }


    /**
    * 方法名:  addGeo
    * 作者/时间: xiahao-2020/6/23
    * 描述: 添加geo地址信息
    * 参数:
     *  longitude:经度
     *  latitude:纬度
     *  addressKey:地址标识
     *  geoKey:geo的key
    * 返回:
    */
    public static void addGeo(double longitude,double latitude,String addressKey,String geoKey) {
        Assert.notNull(addressKey,"param : 'addressKey' not null");
        Assert.notNull(geoKey,"param : 'geoKey' not null");
        log.info("redisGeo===========> add geodata begin, params: longitude:{} latitude:{} addressKey:{} geoKey{}......",longitude,latitude,addressKey,geoKey);

        try {
            redisTemplate.opsForGeo().add(geoKey, new Point(longitude, latitude), addressKey);
            redisTemplate.expire(geoKey,300, TimeUnit.SECONDS);
        }catch (Exception e){
            log.error("CountDistanceUtil 添加geo地址信息 发生异常: error: {}", ExceptionUtils.getStackTrace(e));
            throw new HelloZjException(ExceptionEnum.ADD_REDISGEO_ERROR);
        }
    }

    /**
    * 方法名:  addGeoMap
    * 作者/时间: xiahao-2020/6/24
    * 描述: 以map方式批量添加geo地址信息
    * 参数:
     * map:地址信息的集合
     * geoKey:geo的key
    * 返回:
    */
    public static void addGeoMap(Map<String, Point> map,String geoKey) {
        Assert.notNull(geoKey,"param : 'geoKey' not null");
        Assert.notEmpty(map,"param : 'map' not empty");
        log.info("redisGeo===========> add geomapdata begin, params: map:{} geoKey{}......",map,geoKey);

        try {
            redisTemplate.opsForGeo().add(geoKey,map);
            redisTemplate.expire(geoKey,300, TimeUnit.SECONDS);
        }catch (Exception e){
            log.error("CountDistanceUtil 批量添加geo地址信息 发生异常: error: {}", ExceptionUtils.getStackTrace(e));
            throw new HelloZjException(ExceptionEnum.ADD_REDISGEO_ERROR);
        }
    }



    /**
    * 方法名:  getDist
    * 作者/时间: xiahao-2020/6/23
    * 描述: 计算两点之间的距离
    * 参数:
     * beginAddress:起始地址
     * endAddress:终止地址
     * geoKey:geo的key
    * 返回:
    */
    public static Distance getDist(String beginAddress,String endAddress,String geoKey) {
        Assert.notNull(beginAddress,"param : 'beginAddress' not null");
        Assert.notNull(endAddress,"param : 'endAddress' not null");
        Assert.notNull(geoKey,"param : 'geoKey' not null");

        log.info("redisGeo===========> getDistForTwoAddress count two address's distance begin, params: beginAddress:{} endAddress:{} geoKey{} ......",beginAddress,endAddress,geoKey);
        Distance distance = null;
        try {
            distance = redisTemplate.opsForGeo().distance(geoKey, beginAddress, endAddress,RedisGeoCommands.DistanceUnit.KILOMETERS);
        }catch (Exception e){
            log.error("CountDistanceUtil 计算两点之间的距离 发生异常: error: {}", ExceptionUtils.getStackTrace(e));
            throw new HelloZjException(ExceptionEnum.DIST_REDISGEO_ERROR);
        }
        return distance;
    }

    /**
    * 方法名: getRadius
    * 作者/时间: xiahao-2020/6/23
    * 描述: 获取固定点指定范围内的所有地址信息
    * 参数:
     * dist: 指定的计算距离
     * address:参照的地址
     * sort:结果排序方式
     * geoKey:geo的key
    * 返回:
    */
    public static List<Map<String,Object>> getRadius(double dist,String address,String sort,String geoKey) {
        Assert.notNull(address,"param : 'address' not null");
        Assert.notNull(sort,"param : 'sort' not null");
        Assert.notNull(geoKey,"param : 'geoKey' not null");

        log.info("redisGeo===========> getRadiusForAddress query and sort in radius's address begin, params: dist:{} address:{} sort:{} geoKey{}......",dist,address,sort,geoKey);

        Distance distance = new Distance(dist, Metrics.MILES);
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance();
        //排序
        if ("asc".equals(sort)){
            args.sortAscending();
        }else {
            args.sortDescending();
        }

        List<Map<String,Object>> resultList = new ArrayList<>();
        try{
            GeoResults results = redisTemplate.opsForGeo().radius(geoKey, address, distance, args);
            log.info("redisGeo===========> getRadiusForAddress get resultSet:{}......",results);

            if (results != null){
                getGeoResultData(resultList, results);
            }
        }catch (Exception e){
            log.error("CountDistanceUtil 获取固定点指定范围内的所有地址信息 发生异常: error: {}", ExceptionUtils.getStackTrace(e));
            throw new HelloZjException(ExceptionEnum.RADIUS_REDISGEO_ERROR);
        }
        return resultList;
    }

    /**
    * 方法名:  delAddKey
    * 作者/时间: xiahao-2020/6/23
    * 描述: 删除geo集合内的指定地址key
    * 参数:
     * addressKey:地址标识
     * geoKey:geo的key
    * 返回:
    */
    public static void delAddressKey(String addressKey,String geoKey) {
        Assert.notNull(addressKey,"param : 'addressKey' not null");
        Assert.notNull(geoKey,"param : 'geoKey' not null");

        log.info("redisGeo===========> delAddKey delete addKey begin, params: addressKey:{} geoKey{}......",addressKey,geoKey);
        try {
            redisTemplate.opsForGeo().remove(geoKey,addressKey);
        }catch (Exception e){
            log.error("CountDistanceUtil 删除geo的地址key 发生异常: error: {}", ExceptionUtils.getStackTrace(e));
            throw new HelloZjException(ExceptionEnum.DELADDKEY_REDISGEO_ERROR);
        }
    }

    /**
    * 方法名:  getGeoResultData
    * 作者/时间: xiahao-2020/6/23
    * 描述: 获取地址 以及 距离
    * 参数: ul
    * 返回:
    */
    private static void getGeoResultData(List<Map<String, Object>> resultList, GeoResults results) {
        Iterator iterator = results.iterator();
        while (iterator.hasNext()){
            Map<String,Object> map = new HashMap<>();
            GeoResult next = (GeoResult)iterator.next();
            RedisGeoCommands.GeoLocation content = (RedisGeoCommands.GeoLocation) next.getContent();
            String addKey = (String) content.getName();
            Distance resultDistance = next.getDistance();
            map.put("addKey",addKey);
            map.put("dist",resultDistance);
            resultList.add(map);
        }
    }

    /**
     * 方法名:  getPosition
     * 作者/时间: xiahao-2020/6/23
     * 描述: 根据地址标识获取坐标
     * 参数:
     * addressKey:地址标识
     * geoKey:geo的key
     * 返回:
     */
    public static List<Point> getPosition(String addressKey,String geoKey) {
        Assert.notNull(addressKey,"param : 'addressKey' not null");
        Assert.notNull(geoKey,"param : 'geoKey' not null");

        List<Point> position = null;
        try {
            position = redisTemplate.opsForGeo().position(geoKey, addressKey);
        }catch (Exception e){
            log.error("CountDistanceUtil 根据地址标识获取坐标  发生异常: error: {}", ExceptionUtils.getStackTrace(e));
            throw new HelloZjException(ExceptionEnum.GETPOSITION_REDISGEO_ERROR);
        }
        return position;
    }

    /**
    * 方法名:  delGeoKey
    * 作者/时间: xiahao-2020/6/23
    * 描述: 删除geoKey
    * 参数:
     * geoKey:geo的key
    * 返回:
    */
    public static void delGeoKey(String geoKey) {
        Assert.notNull(geoKey,"param : 'geoKey' not null");
        try {
            redisTemplate.delete(geoKey);
        }catch (Exception e){
            log.error("CountDistanceUtil 删除geoKey  发生异常: error: {}", ExceptionUtils.getStackTrace(e));
            throw new HelloZjException(ExceptionEnum.DELGEOKEY_REDISGEO_ERROR);
        }
    }


    //region 使用示例
/*        Map<String,Point> map = new HashMap<>();
        map.put("nanjing",new Point(160.121212,35.232323));
        map.put("beijing",new Point(161.240899,39.354865));

        CountDistanceUtil.addGeoMap(map,COUNTDISTANCE_KEY);

        Distance dist = CountDistanceUtil.getDist("beijing", "nanjing", COUNTDISTANCE_KEY);
        System.out.println(dist);

        CountDistanceUtil.delAddressKey("beijing",COUNTDISTANCE_KEY);

        List<Point> beijing = CountDistanceUtil.getPosition("beijing", COUNTDISTANCE_KEY);
        System.out.println(beijing.toString());

        CountDistanceUtil.delGeoKey(COUNTDISTANCE_KEY);*/
    //endregion

}