package jnpf.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;

/**
* 文件名 Snowflake.java
* 作者：zhangyb
* 时间：2022/5/10 16:35
* 描述：生成雪花ID类
**/
public class SnowflakeUtil {


    private SnowflakeUtil(){
        throw new IllegalStateException("Utility class");
    }

   /**
   * 方法名 获取雪花ID
   * 作者：zhangyb
   * 时间：2022/5/10 16:35
   * 描述：
   **/
    public static Long nextId(){
        Long workId =  NetUtil.ipv4ToLong(NetUtil.getLocalhostStr())>>16 & 31;
        Snowflake snowflake = IdUtil.getSnowflake(workId, 1);
        return snowflake.nextId();
    }

}
