package com.jzh.erp.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocAutoConfiguration {

    public static final String WWW_DEMO_COM = "http://47.94.103.129:9999/jzhERP-boot/doc.html";

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .info(apiInfo())
                .externalDocs(new ExternalDocumentation()
                        .description("SpringDoc Wiki Documentation")
                        .url("https://springdoc.org/v2"));
    }

    private Info apiInfo() {
        /*return new Info()
                .title("Wen3 Demo API Doc")
                .description("springfox swagger 3.0 demo")
                .version("1.0.0")
                .contact(new Contact()
                        .name("demo")
                        .url("www.demo.com")
                        .email("demo@demo.com")
                )
                .license(new License()
                        .name("Apache 2.0")
                        .url("http://www.apache.org/licenses/LICENSE-2.0.txt")
                );*/
        //"<br/> <H3>jiangwanghong  神秘的天坑研究所群二维码,欢迎进群一起玩耍~" +
        //"<H3/> <br/> <img style=\" heigth:20%; width:20% \" src=\"http://192.168.230.1:9996/jzhERP-boot/static/123456.jpg\"\\>"
        return new Info()
                .title("江天帝ERP Restful Api")
                .description("ERP接口描述   需登录才能访问接口：用户名:jzh   密码：123456")
                .version("1.0.0")
                .contact(new Contact()
                        .name(
                                "江天帝ERP"
                        )
                        .url(WWW_DEMO_COM)
                        .email("541179217@qq.com")
                )
                .license(new License()
                        .name("Apache 2.0")
                        .url("http://www.apache.org/licenses/LICENSE-2.0.txt")
                );
    }
}
