package com.hellozj.order.util;

import com.hellozj.common.exception.HelloZjException;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 文件名: EnumUtil.java
* 作者: xiahao
* 时间: 2020/6/15 14:12
* 描述: 枚举转换工具
*/
public class EnumUtil {
    /**
     * 枚举转map ,枚举value作为map的key, description作为map的value,再将map封装到list返回
     */
    public static <T> List<Map<Object, Object>> EnumToList(Class<T> enumT,String... methodNames) {
        List<Map<Object, Object>> mapList = new ArrayList<>();
        if (!enumT.isEnum()) {
            return mapList;
        }
        T[] enums = enumT.getEnumConstants();
        if (enums == null || enums.length <= 0) {
            return mapList;
        }
        int count = methodNames.length;
        String valueMathod = "value"; //默认接口value方法
        String desMathod = "desc";//默认接口description方法
        if (count >= 1 && !"".equals(methodNames[0])) { //扩展方法
            valueMathod = methodNames[0];
        }
        if (count == 2 && !"".equals(methodNames[1])) {
            desMathod = methodNames[1];
        }
        for (int i = 0, len = enums.length; i < len; i++) {
            Map<Object, Object> enummap = new HashMap<Object, Object>();
            T tobj = enums[i];
            try {
                Object resultValue = getMethodValue(valueMathod, tobj); //获取value值
                if ("".equals(resultValue)) {
                    continue;
                }
                Object resultDes = getMethodValue(desMathod, tobj); //获取description描述值
                if ("".equals(resultDes)) { //如果描述不存在获取属性值
                    resultDes = tobj;
                }
//                enummap.put(resultValue, resultDes + "");
                enummap.put("id", resultValue);
                enummap.put("desc", resultDes);
                mapList.add(enummap);
            } catch (Exception e) {
                throw new HelloZjException(ExceptionUtils.getStackTrace(e));
            }
        }
        return mapList;
    }

    /**
     * 根据反射，通过方法名称获取方法值，忽略大小写的
     */
    private static <T> Object getMethodValue(String methodName, T obj,Object... args) {
        Object resut = "";
        try {
            /********************************* start *****************************************/
            Method[] methods = obj.getClass().getMethods(); //获取方法数组，这里只要共有的方法
            if (methods.length <= 0) {
                return resut;
            }

            Method method = null;
            for (int i = 0, len = methods.length; i < len; i++) {
                if (methods[i].getName().equalsIgnoreCase(methodName)) { //忽略大小写取方法
                    // isHas = true;
                    methodName = methods[i].getName(); //如果存在，则取出正确的方法名称
                    method = methods[i];
                    break;
                }
            }

            /*************************** end ***********************************************/
            if (method == null) {
                return resut;
            }
            resut = method.invoke(obj, args); //方法执行
            if (resut == null) {
                resut = "";
            }
            return resut; //返回结果
        } catch (Exception e) {
            throw new HelloZjException(ExceptionUtils.getStackTrace(e));
//            e.printStackTrace();
        }
//        return resut;
    }
}
