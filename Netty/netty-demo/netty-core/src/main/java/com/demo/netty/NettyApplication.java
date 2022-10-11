package com.demo.netty;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Netty服务
 */
@EnableAsync
@SpringBootApplication
@MapperScan("com.demo.netty.mapper")
public class NettyApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyApplication.class, args);
        System.out.println(
                "====================================================================================================================");
        System.out.println("");
        System.out.println("                                  Netty Running......");
        System.out.println("");
        System.out.println(
                "====================================================================================================================");

    }
}