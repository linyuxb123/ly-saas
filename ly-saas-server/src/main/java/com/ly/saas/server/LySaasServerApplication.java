package com.ly.saas.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.ly.saas"})
@MapperScan(basePackages = {"com.ly.saas.**.mapper"})
@EnableDiscoveryClient
@EnableScheduling
public class LySaasServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LySaasServerApplication.class, args);
    }
}