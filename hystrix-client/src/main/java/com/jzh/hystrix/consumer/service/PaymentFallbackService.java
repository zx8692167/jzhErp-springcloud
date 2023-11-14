package com.jzh.hystrix.consumer.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

//@Service
public class PaymentFallbackService {}/*implements PaymentHystrixService{
    @Override
    public String paymentInfo_OK(Integer id) {
        return "调用服务失败，提示来自：paymentInfo_OK";
    }

    @Override
    public String paymentInfo_TimeOut(Integer id) {
        return "调用服务失败，提示来自：paymentInfo_TimeOut";
    }
}*/
