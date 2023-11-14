package com.jzh.hystrix.consumer.controller;

import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.constants.BusinessConstants;
import com.jzh.erp.datasource.entities.*;
import com.jzh.erp.utils.*;
import com.jzh.hystrix.consumer.service.AccountCenterService;
import com.jzh.hystrix.consumer.service.MerchandiseCenterService;
import com.jzh.hystrix.consumer.service.SystemCenterService;

import com.jzh.hystrix.consumer.service.UserCenterService;
import com.jzh.hystrix.consumer.service.redis.RedisService;
import feign.Response;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/system_center")
@Tag(name = "系统配置中心模块 ")
@Slf4j
public class SystemCenterController {
    @Resource
    SystemCenterService systemCenterService;

    @Resource
    AccountCenterService accountCenterService;


    //    String boot1 ="functions,log,msg,organize,orgaUserRel,platformConfig,role,systemConfig,tenant,user,userBusiness";
//    String boot2 ="account";
    @Resource
    RedisService redisService;

    @Resource
    UserCenterService userCenterService;

    @Resource
    MerchandiseCenterService  merchandiseCenterService;

    private Logger logger = LoggerFactory.getLogger(SystemCenterService.class);

    @GetMapping(value = "/{apiName}/info")
    @Operation(summary = "根据id获取资源接口信息")
    public String getList(@PathVariable("apiName") String apiName,
                          @RequestParam("id") Long id,
                          HttpServletRequest request) throws Exception {
        String boot1 =  String.valueOf(redisService.getObjectByKey("configComponentMap", "jzhERP-boot"));
        String boot2 =  String.valueOf(redisService.getObjectByKey("configComponentMap", "jzhERP-boot2"));
        if(boot1.indexOf(apiName)!=-1){
            return  systemCenterService.getList(apiName,id);
        }else
        if(boot2.indexOf(apiName)!=-1){
            return  accountCenterService.getList2(apiName,id);
        }
            return "资源"+apiName+"不存在";
    }

    @GetMapping(value = "/{apiName}/list")
    @Operation(summary = "获取资源接口信息列表")
    public String getList(@PathVariable("apiName") String apiName,
                          @RequestParam(value = Constants.PAGE_SIZE, required = false) Integer pageSize,
                          @RequestParam(value = Constants.CURRENT_PAGE, required = false) Integer currentPage,
                          @RequestParam(value = Constants.SEARCH, required = false) String search,
                          HttpServletRequest request)throws Exception {

        String boot1 =  String.valueOf(redisService.getObjectByKey("configComponentMap", "jzhERP-boot"));
        String boot2 =  String.valueOf(redisService.getObjectByKey("configComponentMap", "jzhERP-boot2"));
        if(boot1.indexOf(apiName)!=-1){
            return systemCenterService.getList(apiName,pageSize,currentPage,search);
        }else
        if(boot2.indexOf(apiName)!=-1){
            return accountCenterService.getList2(apiName,pageSize,currentPage,search);
        }
        return "资源"+apiName+"不存在";
    }
    // , produces = {"application/javascript", "application/json"}
    @PostMapping(value = "/{apiName}/add")
    @Operation(summary = "新增资源接口")
    @GlobalTransactional(name = "addResource", rollbackFor = Exception.class,timeoutMills = 600000)
    public String addResource(@PathVariable("apiName") String apiName,
                              @RequestBody JSONObject obj, HttpServletRequest request)throws Exception {
        String boot1 =  String.valueOf(redisService.getObjectByKey("configComponentMap", "jzhERP-boot"));
        String boot2 =  String.valueOf(redisService.getObjectByKey("configComponentMap", "jzhERP-boot2"));
        if(boot1.indexOf(apiName)!=-1){
            return systemCenterService.addResource(apiName,obj);
        }
        else if(boot2.indexOf(apiName)!=-1){
            Object userId = redisService.getObjectFromSessionByKey(request,"userId");
            BaseResponseInfo baseResponseInfo = userCenterService.getBasicData(userId.toString(), "UserDepot");

            //新增仓库时给当前用户自动授权
            if(apiName.equals("depot")){
                //插入数据之后返回ID
                String result=accountCenterService.addResource2(apiName,obj);
                JSONObject objs=JSONObject.parseObject(result);
                long depotId = 0;
                /*if((Integer) objs.get("code") == 200 && objs.get("data")!=null) {
                    if (objs != null && ((JSONObject) objs.get("data")).get("id") != null) {*/
                        depotId = Long.parseLong(String.valueOf(((JSONObject) objs.get("data")).get("id")));
                    /*} else {
                        throw new RuntimeException("插入數據失敗，无返回id");
                    }
                }*/
                String ubKey = "[" + depotId + "]";
                if(baseResponseInfo.code == 200 && baseResponseInfo.data!=null) {
                    Map<String, List> mapData = (Map<String, List>)baseResponseInfo.data;
                    List<Map<String, Object>> ubList=mapData.get("userBusinessList");

                    if(ubList ==null || ubList.size() == 0) {
                        JSONObject ubObj = new JSONObject();
                        ubObj.put("type", "UserDepot");
                        ubObj.put("keyId", userId);
                        ubObj.put("value", ubKey);
                        systemCenterService.addResource("userBusiness",ubObj);

                    } else {

                        Map<String, Object> maps= ubList.get(0);
                        UserBusiness ubInfo =JSONObject.parseObject(JSONObject.toJSONString(maps),UserBusiness.class);
                        JSONObject ubObj = new JSONObject();
                        ubObj.put("id", ubInfo.getId());
                        ubObj.put("type", ubInfo.getType());
                        ubObj.put("keyId", ubInfo.getKeyId());
                        ubObj.put("value", ubInfo.getValue() + ubKey);
                        systemCenterService.updateResource("userBusiness",ubObj);
                    }
                    return result;
                }

            }else if(apiName.equals("supplier")){
                Supplier supplier = JSONObject.parseObject(obj.toJSONString(), Supplier.class);
                String result=accountCenterService.addResource2(apiName,obj);
                //新增客户时给当前用户自动授权
                if("客户".equals(supplier.getType())) {
                    //插入数据之后进行查询获取id
                    //Supplier sInfo = supplierMapperEx.getSupplierByNameAndType(supplier.getSupplier(), supplier.getType());
                    Supplier sInfo = merchandiseCenterService.getSupplierByNameAndType(supplier.getSupplier(), supplier.getType());
                    String ubKey = "[" + sInfo.getId() + "]";
                    //List<UserBusiness> ubList = userBusinessService.getBasicData(userId.toString(), "UserCustomer");

                    if(baseResponseInfo.code == 200 && baseResponseInfo.data!=null) {
                        Map<String, List> mapData = (Map<String, List>) baseResponseInfo.data;
                        List<Map<String, Object>> ubList = mapData.get("userBusinessList");
                        if (ubList == null || ubList.size() == 0) {
                            JSONObject ubObj = new JSONObject();
                            ubObj.put("type", "UserCustomer");
                            ubObj.put("keyId", userId);
                            ubObj.put("value", ubKey);
                            systemCenterService.addResource("userBusiness", ubObj);
                        } else {
                            Map<String, Object> maps= ubList.get(0);
                            UserBusiness ubInfo =JSONObject.parseObject(JSONObject.toJSONString(maps),UserBusiness.class);

                            JSONObject ubObj = new JSONObject();
                            ubObj.put("id", ubInfo.getId());
                            ubObj.put("type", ubInfo.getType());
                            ubObj.put("keyId", ubInfo.getKeyId());
                            ubObj.put("value", ubInfo.getValue() + ubKey);
                            systemCenterService.updateResource("userBusiness", ubObj);
                        }

                    }

                }
                return result;
            }
            else{
                return accountCenterService.addResource2(apiName,obj);
            }
        }
        return "资源"+apiName+"不存在";
    }

    @PutMapping(value = "/{apiName}/update", produces = {"application/javascript", "application/json"})
    @Operation(summary = "修改资源接口")
    public String updateResource(@PathVariable("apiName") String apiName,
                                 @RequestBody JSONObject obj, HttpServletRequest request)throws Exception {

        String boot1 =  String.valueOf(redisService.getObjectByKey("configComponentMap", "jzhERP-boot"));
        String boot2 =  String.valueOf(redisService.getObjectByKey("configComponentMap", "jzhERP-boot2"));
        if(boot1.indexOf(apiName)!=-1){
            return systemCenterService.updateResource(apiName,obj);
        }else
        if(boot2.indexOf(apiName)!=-1){
            return accountCenterService.updateResource2(apiName,obj);
        }
        return "资源"+apiName+"不存在";
    }

    @DeleteMapping(value = "/{apiName}/delete", produces = {"application/javascript", "application/json"})
    @Operation(summary = "删除资源接口")
    public String deleteResource(@PathVariable("apiName") String apiName,
                                 @RequestParam("id") Long id, HttpServletRequest request)throws Exception {

        String boot1 =  String.valueOf(redisService.getObjectByKey("configComponentMap", "jzhERP-boot"));
        String boot2 =  String.valueOf(redisService.getObjectByKey("configComponentMap", "jzhERP-boot2"));
        if(boot1.indexOf(apiName)!=-1){
            return systemCenterService.deleteResource(apiName,id);
        }else
        if(boot2.indexOf(apiName)!=-1){
            return accountCenterService.deleteResource2(apiName,id);
        }
        return "资源"+apiName+"不存在";
    }

    @DeleteMapping(value = "/{apiName}/deleteBatch", produces = {"application/javascript", "application/json"})
    @Operation(summary = "批量删除资源接口")
    public String batchDeleteResource(@PathVariable("apiName") String apiName,
                                      @RequestParam("ids") String ids, HttpServletRequest request)throws Exception {

        String boot1 =  String.valueOf(redisService.getObjectByKey("configComponentMap", "jzhERP-boot"));
        String boot2 =  String.valueOf(redisService.getObjectByKey("configComponentMap", "jzhERP-boot2"));
        if(boot1.indexOf(apiName)!=-1){
            return systemCenterService.batchDeleteResource(apiName,ids);
        }else
        if(boot2.indexOf(apiName)!=-1){
            return accountCenterService.batchDeleteResource2(apiName,ids);
        }
        return "资源"+apiName+"不存在";
    }

    @GetMapping(value = "/{apiName}/checkIsNameExist")
    @Operation(summary = "检查资源接口名称是否存在")
    public String checkIsNameExist(@PathVariable("apiName") String apiName,
                                   @RequestParam Long id, @RequestParam(value ="name", required = false) String name,
                                   HttpServletRequest request)throws Exception {

        String boot1 =  String.valueOf(redisService.getObjectByKey("configComponentMap", "jzhERP-boot"));
        String boot2 =  String.valueOf(redisService.getObjectByKey("configComponentMap", "jzhERP-boot2"));
        if(boot1.indexOf(apiName)!=-1){
            return systemCenterService.checkIsNameExist(apiName,id,name);
        }else
        if(boot2.indexOf(apiName)!=-1){
            return accountCenterService.checkIsNameExist2(apiName,id,name);
        }
        return "资源"+apiName+"不存在";
    }



    /**
     * 获取当前租户的配置信息
     * @param request
     * @return
     */
    @GetMapping(value = "/getCurrentInfo")
    @Operation(summary = "获取当前租户的配置信息")
    public BaseResponseInfo getCurrentInfo(HttpServletRequest request) throws Exception {
        return systemCenterService.getCurrentInfo();
    }

    /**
     * 获取文件大小限制
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/fileSizeLimit")
    @Operation(summary = "获取文件大小限制")
    public BaseResponseInfo fileSizeLimit(HttpServletRequest request) throws Exception {

        return systemCenterService.fileSizeLimit();
    }

    /**
     * 文件上传统一方法
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/upload")
    @Operation(summary = "文件上传统一方法")
    public BaseResponseInfo upload(HttpServletRequest request, HttpServletResponse response) {

        return systemCenterService.upload();
    }

    /**
     * 预览图片&下载文件
     * 请求地址：http://localhost:8080/common/static/{financial/afsdfasdfasdf_1547866868179.txt}
     *
     * @param request
     * @param response
     */
    @GetMapping(value = "/static/**")
    @Operation(summary = "预览图片&下载文件")
    public void view(HttpServletRequest request, HttpServletResponse response) {
        Response responseFeign=systemCenterService.view();
        ConvertUtil.feignResp2ServletResp(responseFeign,response);
    }

    /**
     * 获取平台名称
     * @param request
     * @return
     */
    @GetMapping(value = "/getPlatform/name")
    @Operation(summary = "获取平台名称")
    public String getPlatformName(HttpServletRequest request)throws Exception {

        return systemCenterService.getPlatformName();
    }

    /**
     * 获取官方网站地址
     * @param request
     * @return
     */
    @GetMapping(value = "/getPlatform/url")
    @Operation(summary = "获取官方网站地址")
    public String getPlatformUrl(HttpServletRequest request)throws Exception {
        return systemCenterService.getPlatformUrl();
    }

    /**
     * 根据platformKey更新platformValue
     * @param object
     * @param request
     * @return
     */
    @PostMapping(value = "/updatePlatformConfigByKey")
    @Operation(summary = "根据platformKey更新platformValue")
    public String updatePlatformConfigByKey(@RequestBody JSONObject object,
                                            HttpServletRequest request)throws Exception {
        return systemCenterService.updatePlatformConfigByKey(object);
    }

    /**
     * 根据platformKey查询信息
     * @param platformKey
     * @param request
     * @return
     */
    @GetMapping(value = "/getPlatformConfigByKey")
    @Operation(summary = "根据platformKey查询信息")
    public BaseResponseInfo getPlatformConfigByKey(@RequestParam("platformKey") String platformKey,
                                                   HttpServletRequest request)throws Exception {

        return systemCenterService.getPlatformConfigByKey(platformKey);
    }


    @GetMapping("/getMsgByStatus")
    @Operation(summary = "根据状态查询消息")
    public BaseResponseInfo getMsgByStatus(@RequestParam("status") String status,
                                           HttpServletRequest request)throws Exception {

        return systemCenterService.getMsgByStatus(status);
    }

    /**
     * 批量更新状态
     * @param jsonObject
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/batchUpdateStatus")
    @Operation(summary = "批量更新状态")
    public BaseResponseInfo batchUpdateStatus(@RequestBody JSONObject jsonObject,
                                              HttpServletRequest request)throws Exception {

        return systemCenterService.batchUpdateStatus(jsonObject);
    }

    /**
     * 根据状态查询数量
     * @param status
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/getMsgCountByStatus")
    @Operation(summary = "根据状态查询数量")
    public BaseResponseInfo getMsgCountByStatus(@RequestParam("status") String status,
                                                HttpServletRequest request)throws Exception {

        return systemCenterService.getMsgCountByStatus(status);
    }

    /**
     * 根据类型查询数量
     * @param type
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/getMsgCountByType")
    @Operation(summary = "根据类型查询数量")
    public BaseResponseInfo getMsgCountByType(@RequestParam("type") String type,
                                              HttpServletRequest request)throws Exception {

        return systemCenterService.getMsgCountByType(type);
    }

    /**
     * 全部设置未已读
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/readAllMsg")
    @Operation(summary = "全部设置未已读")
    public BaseResponseInfo readAllMsg(HttpServletRequest request)throws Exception {

        return systemCenterService.readAllMsg();
    }











}
