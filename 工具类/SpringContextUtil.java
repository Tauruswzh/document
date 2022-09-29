package com.xx.order.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
* 文件名: SpringContextUtils.java
* 描述: ApplicationContext中的所有bean。换句话说，就是这个类可以直接获取spring配置文件中，所有有引用到的bean对象
*/
@Component
public class SpringContextUtil implements ApplicationContextAware {

    public static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext=applicationContext;

    }


    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /***
    * 方法名: getBean
    * 描述: 通过name获取 Bean.
    * 参数: name
    * 返回: java.lang.Object
    * 异常场景: 
    */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
    * 方法名: getBean
    * 描述: 通过class获取Bean.
    * 参数: clazz
    * 返回: T
    * 异常场景: 
    */
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    /**
    * 方法名: getBean
    * 描述: 通过name,以及Clazz返回指定的Bean
    * 参数: name
     * 参数: clazz
    * 返回: T
    * 异常场景:
    */
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }
}
