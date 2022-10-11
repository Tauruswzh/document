package com.example.netty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Netty服务
 */
@SpringBootApplication
public class NettyExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyExampleApplication.class, args);
        System.out.println(
                "====================================================================================================================");
        System.out.println("");
        System.out.println("                                  Netty example Running......");
        System.out.println("");
        System.out.println(
                "====================================================================================================================");

    }
}