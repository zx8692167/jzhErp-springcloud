package com.jzh.hystrix.consumer.controller;


import com.jzh.hystrix.consumer.service.AccountCenterService;
import com.jzh.hystrix.consumer.service.SystemCenterService;
import com.jzh.hystrix.consumer.service.UserCenterService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
/*@DefaultProperties(defaultFallback = "payment_Global_FallbackMethod")*/
public class OrderHystirxController
{
    @Resource
    private UserCenterService paymentHystrixService;

    @Resource
    SystemCenterService systemCenterService;

    @Resource
    AccountCenterService  accountCenterService;

    @Value("${config.info}")
    private String configInfo;

    @GetMapping("/configInfo")
    private String getConfigInfo() {
        return configInfo;
    }

    @GetMapping("/configInfoBoot")
    private String getConfigInfoBoot() {
        return systemCenterService.getConfigInfo();
    }

    @GetMapping("/configInfoBoot2")
    private String getConfigInfoBoot2() {
        return accountCenterService.getConfigInfo();
    }

    @GetMapping("/consumer/payment/hystrix/ok/{id}")
    public String paymentInfo_OK(@PathVariable("id") Integer id)
    {
        String result = paymentHystrixService.paymentInfo_OK(id);
        return result;
    }

    /*@GetMapping("/consumer/payment/hystrix/{id}")
    @HystrixCommand(fallbackMethod = "paymentTimeOutFallbackMethod",commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="1000")
    })*/
    public String paymentInfo_test(@PathVariable("id") Integer id)
    {
        try {
            TimeUnit.SECONDS.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "线程池:" + Thread.currentThread().getName() + "paymentInfo_TimeOut,id: " + id + "\t" + "O(∩_∩)O，耗费3秒";

    }

    @GetMapping("/consumer/payment/hystrix/timeout/{id}")
    @HystrixCommand(fallbackMethod = "paymentTimeOutFallbackMethod",commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="5000")
    })
    public String paymentInfo_TimeOut(@PathVariable("id") Integer id)
    {
        String result = paymentHystrixService.paymentInfo_TimeOut(id);
        return result;
    }

    public String paymentTimeOutFallbackMethod(@PathVariable("id") Integer id)
    {
        return "我是消费者80,对方支付系统繁忙请10秒钟后再试或者自己运行出错请检查自己,o(╥﹏╥)o";
    }


    @GetMapping("/consumer/payment/hystrix/test/{id}")
    @HystrixCommand(fallbackMethod = "paymentTimeOutFallbackMethod",commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="1000")
    })
    public String paymentInfo_test2(@PathVariable("id") Integer id)
    {
        String result = paymentHystrixService.paymentInfo_TimeOut(id);
    //    String result ="ppppppppppppppppp";

        return result;
    }


    /*public String payment_Global_FallbackMethod(){
        return "这是fallback,请稍后再试。";
    }*/
}
