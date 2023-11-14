package com.jzh.hystrix.consumer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
//import com.alibaba.cloud.seata.GlobalTransactionAutoConfiguration;

@SpringBootApplication
@EnableFeignClients
@EnableHystrix
@MapperScan("com.jzh.erp.datasource.mappers")
@ServletComponentScan
@EnableDiscoveryClient
public class OrderHystrixMain {
    public static void main(String[] args) {
        SpringApplication.run(OrderHystrixMain.class,args);
        System.out.println("Hello world!");
    }
}