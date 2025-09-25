package com.ly.saas.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.ly.saas"})
@MapperScan(basePackages = {"com.ly.saas.**.mapper"})
public class LySaasServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LySaasServerApplication.class, args);
    }
}