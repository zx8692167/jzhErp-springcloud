package com.jzh.hystrix.consumer.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;

import static com.jzh.hystrix.consumer.service.redis.RedisService.ACCESS_SYSTEMCONFIG_TOKEN;
import static com.jzh.hystrix.consumer.service.redis.RedisService.ACCESS_TOKEN;

@Configuration
public class FeignConfigure implements RequestInterceptor {

    /**
     * <p> 在此可以做request参数处理，例如添加头部参数等 </p>
     * @author: xxx
     * @date: xxx
     * @param requestTemplate :
     **/
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        System.out.println("FeignConfigure request.getAttribute"+" "+request.getAttribute(ACCESS_TOKEN));
        System.out.println("FeignConfigure request.getSession().getAttribute"+" "+request.getSession().getAttribute(ACCESS_TOKEN));
        //request.setAttribute(ACCESS_TOKEN,map.get("token"));

        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String value = request.getHeader(name);
                if ("content-length".equals(name)) {
                    continue;
                }
                requestTemplate.header(name, value);
            }
            if(request.getSession().getAttribute(ACCESS_TOKEN)!=null&&request.getAttribute(ACCESS_TOKEN)==null){
                requestTemplate.header(ACCESS_TOKEN, (String)request.getSession().getAttribute(ACCESS_TOKEN));
            }else{
                requestTemplate.header(ACCESS_TOKEN, (String)request.getAttribute(ACCESS_TOKEN));
            }

            /*if(request.getSession().getAttribute(ACCESS_SYSTEMCONFIG_TOKEN)!=null&&request.getAttribute(ACCESS_SYSTEMCONFIG_TOKEN)==null){
                requestTemplate.header(ACCESS_SYSTEMCONFIG_TOKEN, (String)request.getSession().getAttribute(ACCESS_SYSTEMCONFIG_TOKEN));
            }else{
                requestTemplate.header(ACCESS_SYSTEMCONFIG_TOKEN, (String)request.getAttribute(ACCESS_SYSTEMCONFIG_TOKEN));
            }*/

        }
    }
}
