package com.jzh.erp;

import com.jzh.erp.utils.ComputerInfo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.io.IOException;

@SpringBootApplication
@MapperScan("com.jzh.erp.datasource.mappers")
@ServletComponentScan
@EnableScheduling
@EnableDiscoveryClient
@EnableHystrix
@EnableFeignClients
public class ErpApplication{
    public static void main(String[] args) throws IOException {
        //com.gitee.starblues.integration.application.PluginApplication
        //org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration
        /*ConfigurableApplicationContext context = SpringApplication.run(ErpApplication.class, args);
        Environment environment = context.getBean(Environment.class);

        System.out.println("房产ERP启动成功，后端服务API地址：http://" + ComputerInfo.getIpAddr() + ":"
                + environment.getProperty("server.port") + "/jzhERP-boot/doc.html");
 */      // System.out.println("您还需启动前端服务，启动命令：yarn run serve 或 npm run serve，测试用户：jzh，密码：123456");

        SpringApplication.run(ErpApplication.class, args);
        System.out.println("Hello world!");
    }
}
