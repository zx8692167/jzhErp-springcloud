package com.jzh.hystrix.consumer.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.constants.ExceptionConstants;
import com.jzh.erp.datasource.dto.RequestCommonDTO;
import com.jzh.erp.datasource.dto.RequestUserDTO;
import com.jzh.erp.datasource.entities.*;
import com.jzh.erp.datasource.vo.TreeNode;
import com.jzh.erp.utils.*;

import com.jzh.hystrix.consumer.service.UserCenterService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author ji_sheng_hua 中华erp
 */
@RestController
@RequestMapping(value = "/user_center")
@Tag(name = "用户中心模块")
@Slf4j
public class UserCenterController {
    //private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserCenterService userCenterService;


    @PostMapping(value = "/login")
    @Operation(summary = "登录")
    public BaseResponseInfo login(@RequestBody User userParam,
                                  HttpServletRequest request)throws Exception {
        return userCenterService.login(userParam);
    }

    @GetMapping(value = "/getUserSession")
    @Operation(summary = "获取用户信息")
    public BaseResponseInfo getSessionUser(HttpServletRequest request)throws Exception {
        return userCenterService.getSessionUser();
    }

    @GetMapping(value = "/logout")
    @Operation(summary = "退出")
    public BaseResponseInfo logout(HttpServletRequest request, HttpServletResponse response)throws Exception {

        return userCenterService.logout();
    }

    @PostMapping(value = "/resetPwd")
    @Operation(summary = "重置密码")
    public String resetPwd(@RequestBody JSONObject jsonObject,
                           HttpServletRequest request) throws Exception {
        return userCenterService.resetPwd(jsonObject);
    }

    @PutMapping(value = "/updatePwd")
    @Operation(summary = "更新密码")
    public String updatePwd(@RequestBody JSONObject jsonObject, HttpServletRequest request)throws Exception {
        return userCenterService.updatePwd(jsonObject);
    }

    @HystrixCommand(fallbackMethod = "paymentTimeOutFallbackMethod",commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="5000")
    })
    @GetMapping(value = "/getAllList")
    @Operation(summary = "获取全部用户数据列表")
    @GlobalTransactional(name = "getAllListUser", rollbackFor = Exception.class)
    public String getAllList(HttpServletRequest request)throws Exception {
        System.out.println("获取全部用户数据列表");
        /*RequestCommonDTO requestCommonDTO=new RequestCommonDTO();
        requestCommonDTO.setRequest(request);
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();*/
        return userCenterService.getAllList();
    }


    @GetMapping(value = "/getUserList")
    @Operation(summary = "用户列表")
    public JSONArray getUserList(HttpServletRequest request)throws Exception {
        return userCenterService.getUserList();
    }


    @PostMapping("/addUser")
    @Operation(summary = "新增用户")
    @ResponseBody
    public Object addUser(@RequestBody JSONObject obj, HttpServletRequest request)throws Exception{
        /*RequestCommonDTO requestCommonDTO=new RequestCommonDTO();
        requestCommonDTO.setJsonObject(obj);
        requestCommonDTO.setRequest(request);*/
        return userCenterService.addUser(obj);
    }

    @PutMapping("/updateUser")
    @Operation(summary = "修改用户")
    @ResponseBody
    public Object updateUser(@RequestBody JSONObject obj, HttpServletRequest request)throws Exception{
/*
        RequestCommonDTO requestCommonDTO=new RequestCommonDTO();
        requestCommonDTO.setJsonObject(obj);
        requestCommonDTO.setRequest(request);
*/
        return userCenterService.updateUser(obj);
    }


    @PostMapping(value = "/registerUser")
    @Operation(summary = "注册用户")
    public Object registerUser(@RequestBody UserEx ue,
                               HttpServletRequest request)throws Exception{
        return userCenterService.registerUser(ue);
    }


    @PutMapping("/getOrganizationUserTree")
    @Operation(summary = "获取机构用户树")
    public JSONArray getOrganizationUserTree()throws Exception{
        return userCenterService.getOrganizationUserTree();
    }

    @GetMapping(value = "/getCurrentPriceLimit")
    @Operation(summary = "查询当前用户的价格屏蔽")
    public BaseResponseInfo getCurrentPriceLimit(HttpServletRequest request)throws Exception {
        return userCenterService.getCurrentPriceLimit();
    }


    @GetMapping("/getRoleTypeByCurrentUser")
    @Operation(summary = "获取当前用户的角色类型")
    public BaseResponseInfo getRoleTypeByCurrentUser(HttpServletRequest request) {
        return userCenterService.getRoleTypeByCurrentUser();
    }

    @GetMapping(value = "/randomImage/{key}")
    @Operation(summary = "获取随机校验码")
    public BaseResponseInfo randomImage(HttpServletResponse response,@PathVariable String key){
        return userCenterService.randomImage(key);
    }

    @PostMapping(value = "/batchSetStatus")
    @Operation(summary = "用户批量设置状态")
    public String batchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request)throws Exception {
        return userCenterService.batchSetStatus(jsonObject);
    }

    @GetMapping(value = "/infoWithTenant")
    @Operation(summary = "获取当前用户的用户数量和租户信息")
    public BaseResponseInfo randomImage(HttpServletRequest request){
        return userCenterService.randomImage();
    }





    /**
     * 获取信息
     * @param keyId
     * @param type
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getBasicData")
    @Operation(summary = "获取信息")
    @GlobalTransactional(name = "getBasicData", rollbackFor = Exception.class,timeoutMills = 6000)
    public BaseResponseInfo getBasicData(@RequestParam(value = "KeyId") String keyId,
                                         @RequestParam(value = "Type",required=false) String type,
                                         HttpServletRequest request)throws Exception {
         BaseResponseInfo getBasicData = userCenterService.getBasicData(keyId,type);
        return getBasicData;
    }

    /**
     * 校验存在
     * @param type
     * @param keyId
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/checkIsValueExist")
    @Operation(summary = "校验存在")
    public String checkIsValueExist(@RequestParam(value ="type", required = false) String type,
                                    @RequestParam(value ="keyId", required = false) String keyId,
                                    HttpServletRequest request)throws Exception {
        return userCenterService.checkIsValueExist(type,type);
    }

    /**
     * 更新角色的按钮权限
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/updateBtnStr")
    @Operation(summary = "更新角色的按钮权限")
    public BaseResponseInfo updateBtnStr(@RequestBody JSONObject jsonObject,
                                         HttpServletRequest request)throws Exception {
        return userCenterService.updateBtnStr(jsonObject);
    }

    @GetMapping(value = "/findById")
    @Operation(summary = "根据id来查询机构信息")
    public BaseResponseInfo findById(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        return userCenterService.findById(id);
    }

    /**
     * create by: jwh
     * description:
     * 获取机构树数据
     * create time: 2019/2/19 11:49
     * @Param:
     * @return com.alibaba.fastjson.JSONArray
     */
    @PutMapping(value = "/getOrganizationTree")
    @Operation(summary = "获取机构树数据")
    public JSONArray getOrganizationTree(@RequestParam("id") Long id) throws Exception{
        return userCenterService.getOrganizationTree(id);
    }
    /**
     * create by: jwh
     * description:
     *  新增机构信息
     * create time: 2019/2/19 17:17
     * @Param: beanJson
     * @return java.lang.Object
     */
    @PostMapping(value = "/addOrganization")
    @Operation(summary = "新增机构信息")
    public Object addOrganization(@RequestParam("info") String beanJson) throws Exception {
        return userCenterService.addOrganization(beanJson);
    }
    /**
     * create by: jwh
     * description:
     *  修改机构信息
     * create time: 2019/2/20 9:30
     * @Param: beanJson
     * @return java.lang.Object
     */
    @PostMapping(value = "/editOrganization")
    @Operation(summary = "修改机构信息")
    public Object editOrganization(@RequestParam("info") String beanJson) throws Exception {
        return userCenterService.editOrganization(beanJson);
    }


    /**
     * 批量设置状态-启用或者禁用
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/tenant/batchSetStatus")
    @Operation(summary = "租户批量设置状态")
    public String tenantBatchSetStatus(@RequestBody JSONObject jsonObject,
                                       HttpServletRequest request)throws Exception {
        return userCenterService.tenantBatchSetStatus(jsonObject);
    }


    /**
     * 角色对应应用显示
     * @param request
     * @return
     */
    @GetMapping(value = "/findUserRole")
    @Operation(summary = "查询用户的角色")
    public JSONArray findUserRole(@RequestParam("UBType") String type, @RequestParam("UBKeyId") String keyId,
                                  HttpServletRequest request)throws Exception {
        return userCenterService.findUserRole(type,keyId);
    }

    @GetMapping(value = "/role/allList")
    @Operation(summary = "角色查询全部角色列表")
    public List<Role> allList(HttpServletRequest request)throws Exception {
        return userCenterService.allList();
    }

    /**
     * 批量设置状态-启用或者禁用
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/role/batchSetStatus")
    @Operation(summary = "角色批量设置状态")
    public String roleBatchSetStatus(@RequestBody JSONObject jsonObject,
                                     HttpServletRequest request)throws Exception {
        return userCenterService.roleBatchSetStatus(jsonObject);
    }




    @GetMapping(value = "/checkIsNumberExist")
    @Operation(summary = "功能检查编号是否存在")
    public String checkIsNumberExist(@RequestParam Long id,
                                     @RequestParam(value ="number", required = false) String number,
                                     HttpServletRequest request)throws Exception {
        return userCenterService.checkIsNumberExist(id,number);
    }

    /**
     * 根据父编号查询菜单
     * @param jsonObject
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/findMenuByPNumber")
    @Operation(summary = "根据父编号查询菜单")
    public JSONArray findMenuByPNumber(@RequestBody JSONObject jsonObject,
                                       HttpServletRequest request)throws Exception {
        return userCenterService.findMenuByPNumber(jsonObject);
    }

    /**
     * 角色对应功能显示
     * @param request
     * @return
     */
    @GetMapping(value = "/findRoleFunction")
    @Operation(summary = "角色对应功能显示")
    public JSONArray findRoleFunction(@RequestParam("UBType") String type, @RequestParam("UBKeyId") String keyId,
                                      HttpServletRequest request)throws Exception {

        return userCenterService.findRoleFunction(type,keyId);
    }

    /**
     * 根据id列表查找功能信息
     * @param roleId
     * @param request
     * @return
     */
    @GetMapping(value = "/findRoleFunctionsById")
    @Operation(summary = "根据id列表查找功能信息")
    public BaseResponseInfo findByIds(@RequestParam("roleId") Long roleId,
                                      HttpServletRequest request)throws Exception {
        return userCenterService.findByIds(roleId);
    }

















    public String paymentTimeOutFallbackMethod()
    {
        return "系统繁忙请10秒钟后再试或者自己运行出错请检查自己,o(╥﹏╥)o";
    }







    /**
     * 这个方式是先将点赞数据写入缓存，再从缓存写入数据库。实际上应该先写入数据库（写入时用锁的方式保证并发问题），再写入缓存。查询从缓存直接查。
     * 其它场景大批量数据一次调用接口进入处理流程的时候（或者在service层）----使用线程池分批量多线程处理并发问题
     * @param userLike
     * @param request
     */
    @PostMapping(value = "/api/like")
    @Operation(summary = "点赞或取消点赞")
    public BaseResponseInfo doLike(@RequestBody UserLike userLike, HttpServletRequest request) {
        return userCenterService.doLike(userLike);
    }











}
