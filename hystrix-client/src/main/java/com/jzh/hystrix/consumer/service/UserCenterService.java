package com.jzh.hystrix.consumer.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.datasource.entities.Role;
import com.jzh.erp.datasource.entities.User;
import com.jzh.erp.datasource.entities.UserEx;
import com.jzh.erp.datasource.entities.UserLike;
import com.jzh.erp.utils.BaseResponseInfo;
import com.jzh.hystrix.consumer.config.FeignConfigure;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
@FeignClient(value = "CLOUD-JZH-ERP",configuration = FeignConfigure.class)
public interface UserCenterService {

    /**
     *  以下接口为用户中心模块
     * @param id
     * @return
     */

    @GetMapping("/jzhERP-boot/user/payment/hystrix/ok/{id}")
    String paymentInfo_OK(@PathVariable("id") Integer id);

    @GetMapping("/jzhERP-boot/user/payment/hystrix/timeout/{id}")
    String paymentInfo_TimeOut(@PathVariable("id") Integer id);

    @PostMapping(value = "/jzhERP-boot/user/login")
    BaseResponseInfo login(@RequestBody User userParam);

    @PostMapping(value = "/jzhERP-boot/api/like")
    BaseResponseInfo doLike(@RequestBody UserLike userLike);

    @GetMapping(value = "/jzhERP-boot/user/getUserSession")
     BaseResponseInfo getSessionUser();

    @GetMapping(value = "/jzhERP-boot/user/logout")
     BaseResponseInfo logout();

    @PostMapping(value = "/jzhERP-boot/user/resetPwd")
     String resetPwd( JSONObject jsonObject);

    @PutMapping(value = "/jzhERP-boot/user/updatePwd")
     String updatePwd( JSONObject jsonObject);

    @GetMapping(value = "/jzhERP-boot/user/getAllList")
    String getAllList();


    @GetMapping(value = "/jzhERP-boot/user/getUserList")
     JSONArray getUserList();

    @PostMapping("/jzhERP-boot/user/addUser")
     Object addUser(@RequestBody JSONObject obj);

    @PutMapping("/jzhERP-boot/user/updateUser")
     Object updateUser(@RequestBody JSONObject obj);


    @PostMapping(value = "/jzhERP-boot/user/registerUser")
     Object registerUser( UserEx ue);


    @PutMapping("/jzhERP-boot/user/getOrganizationUserTree")
     JSONArray getOrganizationUserTree();

    @GetMapping(value = "/jzhERP-boot/user/getCurrentPriceLimit")
     BaseResponseInfo getCurrentPriceLimit();


    @GetMapping("/jzhERP-boot/user/getRoleTypeByCurrentUser")
     BaseResponseInfo getRoleTypeByCurrentUser();

    @GetMapping(value = "/jzhERP-boot/user/randomImage/{key}")
     BaseResponseInfo randomImage(@PathVariable String key);

    @PostMapping(value = "/jzhERP-boot/user/batchSetStatus")
     String batchSetStatus( JSONObject jsonObject);

    @GetMapping(value = "/jzhERP-boot/user/infoWithTenant")
     BaseResponseInfo randomImage();



    @GetMapping(value = "/jzhERP-boot/userBusiness/getBasicData")
     BaseResponseInfo getBasicData(@RequestParam(value = "KeyId") String keyId,
                                         @RequestParam(value = "Type",required=false) String type);

    @GetMapping(value = "/jzhERP-boot/userBusiness/checkIsValueExist")
     String checkIsValueExist(@RequestParam(value ="type", required = false) String type,
                                    @RequestParam(value ="keyId", required = false) String keyId);
    @PostMapping(value = "/jzhERP-boot/userBusiness/updateBtnStr")
     BaseResponseInfo updateBtnStr(@RequestBody JSONObject jsonObject);

    @GetMapping(value = "/jzhERP-boot/organization/findById")
     BaseResponseInfo findById(@RequestParam("id") Long id);


    @PutMapping(value = "/jzhERP-boot/organization/getOrganizationTree")
     JSONArray getOrganizationTree(@RequestParam("id") Long id);


    @PostMapping(value = "/jzhERP-boot/organization/addOrganization")
     Object addOrganization(@RequestParam("info") String beanJson);

    @PostMapping(value = "/jzhERP-boot/organization/editOrganization")
     Object editOrganization(@RequestParam("info") String beanJson);

    @PostMapping(value = "/jzhERP-boot/tenant/batchSetStatus")
     String tenantBatchSetStatus(@RequestBody JSONObject jsonObject);


    @GetMapping(value = "/jzhERP-boot/findUserRole")
     JSONArray findUserRole(@RequestParam("UBType") String type, @RequestParam("UBKeyId") String keyId);

    @GetMapping(value = "/jzhERP-boot/role/allList")
     List<Role> allList();

    @PostMapping(value = "/jzhERP-boot/role/batchSetStatus")
     String roleBatchSetStatus(@RequestBody JSONObject jsonObject);



    @GetMapping(value = "/jzhERP-boot/function/checkIsNumberExist")
     String checkIsNumberExist(@RequestParam Long id,
                                     @RequestParam(value ="number", required = false) String number);

    @PostMapping(value = "/jzhERP-boot/function/findMenuByPNumber")
     JSONArray findMenuByPNumber(@RequestBody JSONObject jsonObject);

    @GetMapping(value = "/jzhERP-boot/function/findRoleFunction")
    JSONArray findRoleFunction(@RequestParam("UBType") String type, @RequestParam("UBKeyId") String keyId);

    @GetMapping(value = "/jzhERP-boot/function/findRoleFunctionsById")
     BaseResponseInfo findByIds(@RequestParam("roleId") Long roleId);




}
