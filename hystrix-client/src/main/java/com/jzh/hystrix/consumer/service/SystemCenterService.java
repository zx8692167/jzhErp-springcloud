package com.jzh.hystrix.consumer.service;

import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.utils.BaseResponseInfo;
import com.jzh.erp.utils.Constants;
import com.jzh.hystrix.consumer.config.FeignConfigure;
import feign.Response;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@FeignClient(value = "CLOUD-JZH-ERP",configuration = FeignConfigure.class)
public interface SystemCenterService {
    /**
     *  以下接口为系统配置中心模块
     */

    @GetMapping(value = "/jzhERP-boot/systemConfig/configInfo")
    String getConfigInfo();

    @GetMapping(value = "/jzhERP-boot/{apiName}/info")
     String getList(@PathVariable("apiName") String apiName,
                          @RequestParam("id") Long id);


    @GetMapping(value = "/jzhERP-boot/{apiName}/list")
     String getList(@PathVariable("apiName") String apiName,
                          @RequestParam(value = Constants.PAGE_SIZE, required = false) Integer pageSize,
                          @RequestParam(value = Constants.CURRENT_PAGE, required = false) Integer currentPage,
                          @RequestParam(value = Constants.SEARCH, required = false) String search);


    @PostMapping(value = "/jzhERP-boot/{apiName}/add", produces = {"application/javascript", "application/json"})
     String addResource(@PathVariable("apiName") String apiName,
                              @RequestBody JSONObject obj);

    @PutMapping(value = "/jzhERP-boot/{apiName}/update", produces = {"application/javascript", "application/json"})
     String updateResource(@PathVariable("apiName") String apiName,
                                 @RequestBody JSONObject obj);

    @DeleteMapping(value = "/jzhERP-boot/{apiName}/delete", produces = {"application/javascript", "application/json"})
     String deleteResource(@PathVariable("apiName") String apiName,
                                 @RequestParam("id") Long id);

    @DeleteMapping(value = "/jzhERP-boot/{apiName}/deleteBatch", produces = {"application/javascript", "application/json"})
     String batchDeleteResource(@PathVariable("apiName") String apiName,
                                      @RequestParam("ids") String ids);

    @GetMapping(value = "/jzhERP-boot/{apiName}/checkIsNameExist")
     String checkIsNameExist(@PathVariable("apiName") String apiName,
                                   @RequestParam Long id, @RequestParam(value ="name", required = false) String name);


    @GetMapping(value = "/jzhERP-boot/systemConfig/getCurrentInfo")
     BaseResponseInfo getCurrentInfo();

    @GetMapping(value = "/jzhERP-boot/systemConfig/fileSizeLimit")
     BaseResponseInfo fileSizeLimit();

    @PostMapping(value = "/jzhERP-boot/systemConfig/upload")
     BaseResponseInfo upload();

    @GetMapping(value = "/jzhERP-boot/systemConfig/static/**")
    Response view();

    @GetMapping(value = "/jzhERP-boot/platformConfig/getPlatform/name")
     String getPlatformName();

    @GetMapping(value = "/jzhERP-boot/platformConfig/getPlatform/url")
     String getPlatformUrl();

    @PostMapping(value = "/jzhERP-boot/platformConfig/updatePlatformConfigByKey")
     String updatePlatformConfigByKey(@RequestBody JSONObject object);

    @GetMapping(value = "/jzhERP-boot/platformConfig/getPlatformConfigByKey")
     BaseResponseInfo getPlatformConfigByKey(@RequestParam("platformKey") String platformKey);

    @GetMapping("/jzhERP-boot/msg/getMsgByStatus")
     BaseResponseInfo getMsgByStatus(@RequestParam("status") String status);

    @PostMapping("/jzhERP-boot/msg/batchUpdateStatus")
     BaseResponseInfo batchUpdateStatus(@RequestBody JSONObject jsonObject);

    @GetMapping("/jzhERP-boot/msg/getMsgCountByStatus")
     BaseResponseInfo getMsgCountByStatus(@RequestParam("status") String status);

    @GetMapping("/jzhERP-boot/msg/getMsgCountByType")
     BaseResponseInfo getMsgCountByType(@RequestParam("type") String type);

    @PostMapping("/jzhERP-boot/msg/readAllMsg")
     BaseResponseInfo readAllMsg();





}
