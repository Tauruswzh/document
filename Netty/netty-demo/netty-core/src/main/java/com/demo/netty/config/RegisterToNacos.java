//package com.demo.netty.config;
//
//import com.alibaba.nacos.api.exception.NacosException;
//import com.alibaba.nacos.api.naming.NamingFactory;
//import com.alibaba.nacos.api.naming.NamingService;
//import com.demo.netty.entity.enums.AppConstant;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.springframework.boot.context.event.ApplicationStartedEvent;
//import org.springframework.context.ApplicationListener;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.Objects;
//import java.util.Properties;
//
///**
// * 注册到nacos
// */
//@Slf4j
////@Component
//public class RegisterToNacos implements ApplicationListener<ApplicationStartedEvent> {
//
//    @Override
//    public void onApplicationEvent(ApplicationStartedEvent event) {
//        try {
//            String serverList = event.getApplicationContext().getEnvironment().getProperty("spring.cloud.nacos.discovery.server-addr");
//            String groupName = event.getApplicationContext().getEnvironment().getProperty("spring.cloud.nacos.discovery.group");
//            String namespace = event.getApplicationContext().getEnvironment().getProperty("spring.cloud.nacos.discovery.namespace");
//            String ip = InetAddress.getLocalHost().getHostAddress();
//            String port = event.getApplicationContext().getEnvironment().getProperty("health.message.netty.port");
//
//            Properties properties = new Properties();
//            properties.setProperty("serverAddr", serverList);
//            if (Objects.nonNull(namespace)) {
//                properties.setProperty("namespace", namespace);
//            }
//            //获取nacos服务
//            NamingService namingService = NamingFactory.createNamingService(properties);
//            //将服务注册到注册中心
//            namingService.registerInstance(AppConstant.APP_MESSAGE_WEBSOCKET, groupName, ip, Integer.parseInt(port));
//        } catch (NacosException e) {
//            log.error("注册nacos失败{}", ExceptionUtils.getStackTrace(e));
//        } catch (UnknownHostException e) {
//            log.error("获取本机IP失败{}", ExceptionUtils.getStackTrace(e));
//        }
//    }
//}