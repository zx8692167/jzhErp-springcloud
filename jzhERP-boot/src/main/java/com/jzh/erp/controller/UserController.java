package com.jzh.erp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.constants.BusinessConstants;
import com.jzh.erp.constants.ExceptionConstants;
import com.jzh.erp.datasource.entities.*;
import com.jzh.erp.datasource.vo.TreeNodeEx;
import com.jzh.erp.exception.BusinessParamCheckingException;
import com.jzh.erp.service.log.LogService;
import com.jzh.erp.service.orgaUserRel.OrgaUserRelService;
import com.jzh.erp.service.redis.RedisService;
import com.jzh.erp.service.role.RoleService;
import com.jzh.erp.service.tenant.TenantService;
import com.jzh.erp.service.user.UserService;
import com.jzh.erp.service.userBusiness.UserBusinessService;
import com.jzh.erp.utils.*;
import com.jzh.erp.utils.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletOutputStream;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;

import static com.jzh.erp.service.redis.RedisService.ACCESS_TOKEN;

/**
 * @author ji_sheng_hua 中华erp
 */
@RestController
@RequestMapping(value = "/user")
@Tag(name = "用户管理")
public class UserController {
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${manage.roleId}")
    private Integer manageRoleId;

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Resource
    private TenantService tenantService;

    @Resource
    private LogService logService;

    @Resource
    private RedisService redisService;

    @Resource
    UserBusinessService userBusinessService;

    @Resource
    OrgaUserRelService orgaUserRelService;

    private static final String TEST_USER = "jzh";
    private static String SUCCESS = "操作成功";
    private static String ERROR = "操作失败";
    private static final String HTTP = "http://";
    private static final String CODE_OK = "200";
    private static final String BASE_CHECK_CODES = "qwertyuiplkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM1234567890";

    /**
     * redis 失效时间跟 session会话失效时间不一致
     * @param userParam
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/login")
    @Operation(summary = "登录")
    public BaseResponseInfo login(@RequestBody User userParam,
                                  HttpServletRequest request)throws Exception {
        logger.info("============用户登录 login 方法调用开始==============");
        String msgTip = "";
        User user=null;
        BaseResponseInfo res = new BaseResponseInfo();
        try {

            String loginName = userParam.getLoginName().trim();
            String password = userParam.getPassword().trim();
            //判断用户是否已经登录过，登录过不再处理
            Object userId = redisService.getObjectFromSessionByKey(request,"userId");
            if (userId != null) {
                logger.info("====用户已经登录过, login 方法调用结束====");
                msgTip = "user already login";
            }
            //获取用户状态
            int userStatus = -1;
            try {
                redisService.deleteObjectBySession(request,"userId");
                userStatus = userService.validateUser(loginName, password);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(">>>>>>>>>>>>>用户  " + loginName + " 登录 login 方法 访问服务层异常====", e);
                msgTip = "access service exception";
            }
            String token = UUID.randomUUID().toString().replaceAll("-", "") + "";
            switch (userStatus) {
                case ExceptionCodeConstants.UserExceptionCode.USER_NOT_EXIST:
                    msgTip = "user is not exist";
                    break;
                case ExceptionCodeConstants.UserExceptionCode.USER_PASSWORD_ERROR:
                    msgTip = "user password error";
                    break;
                case ExceptionCodeConstants.UserExceptionCode.BLACK_USER:
                    msgTip = "user is black";
                    break;
                case ExceptionCodeConstants.UserExceptionCode.USER_ACCESS_EXCEPTION:
                    msgTip = "access service error";
                    break;
                case ExceptionCodeConstants.UserExceptionCode.BLACK_TENANT:
                    msgTip = "tenant is black";
                    break;
                case ExceptionCodeConstants.UserExceptionCode.EXPIRE_TENANT:
                    msgTip = "tenant is expire";
                    break;
                case ExceptionCodeConstants.UserExceptionCode.USER_CONDITION_FIT:
                    msgTip = "user can login";
                    //验证通过 ，可以登录，放入session，记录登录日志
                    user = userService.getUserByLoginName(loginName);
                    if(user.getTenantId()!=null) {
                        token = token + "_" + user.getTenantId();
                    }
                    redisService.storageObjectBySession(token,"userId",user.getId());
                    redisService.storageObjectBySession(token,"loginName",user.getLoginName());
                    redisService.storageObjectBySession(token,"username",user.getUsername());
                    request.getSession().setAttribute(ACCESS_TOKEN,token);
                    //response.setHeader(ACCESS_TOKEN, token);
                    break;
                default:
                    break;
            }
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("msgTip", msgTip);
            if(user!=null){
                Role role=userService.getRoleTypeByUserId(user.getId());
                String roleType = role.getType(); //角色类型
                redisService.storageObjectBySession(token,"role",JSONObject.toJSONString(role));
                redisService.storageObjectBySession(token,"roleType",roleType);
                redisService.storageObjectBySession(token,"clientIp", Tools.getLocalIp(request));
                logService.insertLogWithUserId(user.getId(), user.getTenantId(), "用户",
                        new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_LOGIN).append(user.getLoginName()).toString(),
                        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
                JSONArray btnStrArr = userService.getBtnStrArrById(user.getId());
                data.put("token", token);
                data.put("user", user);
                //用户的按钮权限
                if(!"admin".equals(user.getLoginName())){
                    data.put("userBtn", btnStrArr);
                }
                data.put("roleType", roleType);

                    if(user!=null) {
                        List<UserBusiness> list = userBusinessService.getBasicData(user.getId().toString(), null);
                        if (list != null) {
                            Map<String, Object> basicDataConfig = new HashMap<String, Object>();
                            for (UserBusiness userBusiness : list) {
                                basicDataConfig.put(userBusiness.getType(), userBusiness.getValue());
                            }
                            redisService.storageObjectBySession(token, "basicDataConfig", JSONObject.toJSONString(basicDataConfig));
                        }

                        String users=orgaUserRelService.getUserIdListByUserId(user.getId());
                        redisService.storageObjectBySession(token, "orgParentUsers", users);
                    }





            }
            res.code = 200;
            res.data = data;

            /*Map<String, String> map = new HashMap<String, String>();
            Enumeration paramNames = request.getParameterNames();
            while(paramNames.hasMoreElements()) {
                String paramName = (String) paramNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                if (paramValues.length == 1) {
                    if ( paramValues[0].length() != 0) {
                        map.put(paramName, paramValues[0]);
                    }
                }
            }
            System.out.println("----------------------------------------------"+map+"---------------------------------------");


            logger.info(map.toString());*/
            //logger.info(response.);
            logger.info("===============用户登录 login 方法调用结束===============");
        } catch(Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            res.code = 500;
            res.data = "用户登录失败";
        }
        return res;
    }

    @GetMapping(value = "/getUserSession")
    @Operation(summary = "获取用户信息")
    public BaseResponseInfo getSessionUser(HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Map<String, Object> data = new HashMap<>();
            Long userId = Long.parseLong(redisService.getObjectFromSessionByKey(request,"userId").toString());
            User user = userService.getUser(userId);
            user.setPassword(null);
            data.put("user", user);
            res.code = 200;
            res.data = data;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取session失败";
        }
        return res;
    }

    @GetMapping(value = "/logout")
    @Operation(summary = "退出")
    public BaseResponseInfo logout(HttpServletRequest request, HttpServletResponse response)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            redisService.deleteObjectBySession(request,"userId");
            redisService.deleteObjectBySession(request,"roleType");
            redisService.deleteObjectBySession(request,"clientIp");
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "退出失败";
        }
        return res;
    }

    @PostMapping(value = "/resetPwd")
    @Operation(summary = "重置密码")
    public String resetPwd(@RequestBody JSONObject jsonObject,
                                     HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        Long id = jsonObject.getLong("id");
        String password = "123456";
        String md5Pwd = Tools.md5Encryp(password);
        int update = userService.resetPwd(md5Pwd, id);
        if(update > 0) {
            return ResponseJsonUtil.returnJson(objectMap, SUCCESS, ErpInfo.OK.code);
        } else {
            return ResponseJsonUtil.returnJson(objectMap, ERROR, ErpInfo.ERROR.code);
        }
    }

    @PutMapping(value = "/updatePwd")
    @Operation(summary = "更新密码")
    public String updatePwd(@RequestBody JSONObject jsonObject, HttpServletRequest request)throws Exception {
        Integer flag = 0;
        Map<String, Object> objectMap = new HashMap<String, Object>();
        try {
            String info = "";
            Long userId = jsonObject.getLong("userId");
            String oldpwd = jsonObject.getString("oldpassword");
            String password = jsonObject.getString("password");
            User user = userService.getUser(userId);
            //必须和原始密码一致才可以更新密码
            if (oldpwd.equalsIgnoreCase(user.getPassword())) {
                user.setPassword(password);
                flag = userService.updateUserByObj(user); //1-成功
                info = "修改成功";
            } else {
                flag = 2; //原始密码输入错误
                info = "原始密码输入错误";
            }
            objectMap.put("status", flag);
            if(flag > 0) {
                return ResponseJsonUtil.returnJson(objectMap, info, ErpInfo.OK.code);
            } else {
                return ResponseJsonUtil.returnJson(objectMap, ERROR, ErpInfo.ERROR.code);
            }
        } catch (Exception e) {
            logger.error(">>>>>>>>>>>>>修改用户ID为 ： " + jsonObject.getLong("userId") + "密码信息失败", e);
            flag = 3;
            objectMap.put("status", flag);
            return ResponseJsonUtil.returnJson(objectMap, ERROR, ErpInfo.ERROR.code);
        }
    }

    /**
     * 获取全部用户数据列表
     * @param request
     * @return
     */
    @GetMapping(value = "/getAllList")
    @Operation(summary = "获取全部用户数据列表")
    public BaseResponseInfo getAllList(HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Map<String, Object> data = new HashMap<String, Object>();
            List<User> dataList = userService.getUser();
            if(dataList!=null) {
                data.put("userList", dataList);
            }
            res.code = 200;
            res.data = data;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取失败";
        }
        return res;
    }

    /**
     * 测试 hystrix feign
     * @param id
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/payment/hystrix/timeout/{id}")
    public String paymentInfo_TimeOut(@PathVariable("id") Integer id) throws InterruptedException {
        String result = userService.paymentInfo_TimeOut(id);
        logger.info("****result: " + result);
        return result;
    }

    @GetMapping("/payment/hystrix/ok/{id}")
    public String paymentInfo_OK(@PathVariable("id") Integer id) {
        String result = userService.paymentInfo_ok(id);
        logger.info("****result: " + result);
        return result;
    }

    /**
     * 用户列表，用于用户下拉框
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getUserList")
    @Operation(summary = "用户列表")
    public JSONArray getUserList(HttpServletRequest request)throws Exception {
        JSONArray dataArray = new JSONArray();
        try {
            List<User> dataList = userService.getUser();
            if (null != dataList) {
                for (User user : dataList) {
                    JSONObject item = new JSONObject();
                    item.put("id", user.getId());
                    item.put("userName", user.getUsername());
                    dataArray.add(item);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return dataArray;
    }

    /**
     * create by: jwh
     * description:
     *  新增用户及机构和用户关系
     * create time: 2019/3/8 16:06
     * @Param: beanJson
     * @return java.lang.Object
     */
    @PostMapping("/addUser")
    @Operation(summary = "新增用户")
    @ResponseBody
    public Object addUser(@RequestBody JSONObject obj, HttpServletRequest request)throws Exception{
        JSONObject result = ExceptionConstants.standardSuccess();
        User userInfo = userService.getCurrentUser();
        Tenant tenant = tenantService.getTenantByTenantId(userInfo.getTenantId());
        Long count = userService.countUser(null,null);
        if(tenant!=null) {
            if(count>= tenant.getUserNumLimit()) {
                throw new BusinessParamCheckingException(ExceptionConstants.USER_OVER_LIMIT_FAILED_CODE,
                        ExceptionConstants.USER_OVER_LIMIT_FAILED_MSG);
            } else {
                UserEx ue= JSONObject.parseObject(obj.toJSONString(), UserEx.class);
                userService.addUserAndOrgUserRel(ue, request);
            }
        }
        return result;
    }

    /**
     * create by: jwh
     * description:
     *  修改用户及机构和用户关系
     * create time: 2019/3/8 16:06
     * @Param: beanJson
     * @return java.lang.Object
     */
    @PutMapping("/updateUser")
    @Operation(summary = "修改用户")
    @ResponseBody
    public Object updateUser(@RequestBody JSONObject obj, HttpServletRequest request)throws Exception{
        JSONObject result = ExceptionConstants.standardSuccess();
        UserEx ue= JSONObject.parseObject(obj.toJSONString(), UserEx.class);
        userService.updateUserAndOrgUserRel(ue, request);
        return result;
    }

    /**
     * 注册用户
     * @param ue
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/registerUser")
    @Operation(summary = "注册用户")
    public Object registerUser(@RequestBody UserEx ue,
                               HttpServletRequest request)throws Exception{
        JSONObject result = ExceptionConstants.standardSuccess();
        ue.setUsername(ue.getLoginName());
        userService.checkLoginName(ue); //检查登录名
        ue = userService.registerUser(ue,manageRoleId,request);
        return result;
    }

    /**
     * 获取机构用户树
     * @return
     * @throws Exception
     */
    @PutMapping("/getOrganizationUserTree")
    @Operation(summary = "获取机构用户树")
    public JSONArray getOrganizationUserTree()throws Exception{
        JSONArray arr=new JSONArray();
        List<TreeNodeEx> organizationUserTree= userService.getOrganizationUserTree();
        if(organizationUserTree!=null&&organizationUserTree.size()>0){
            for(TreeNodeEx node:organizationUserTree){
                String str=JSON.toJSONString(node);
                JSONObject obj=JSON.parseObject(str);
                arr.add(obj) ;
            }
        }
        return arr;
    }

    @GetMapping(value = "/getCurrentPriceLimit")
    @Operation(summary = "查询当前用户的价格屏蔽")
    public BaseResponseInfo getCurrentPriceLimit(HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Map<String, Object> data = new HashMap<>();
            String priceLimit = roleService.getCurrentPriceLimit(request);
            data.put("priceLimit", priceLimit);
            res.code = 200;
            res.data = data;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取session失败";
        }
        return res;
    }

    /**
     * 获取当前用户的角色类型
     * @param request
     * @return
     */
    @GetMapping("/getRoleTypeByCurrentUser")
    @Operation(summary = "获取当前用户的角色类型")
    public BaseResponseInfo getRoleTypeByCurrentUser(HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Map<String, Object> data = new HashMap<String, Object>();
            String roleType = redisService.getObjectFromSessionByKey(request,"roleType").toString();
            data.put("roleType", roleType);
            res.code = 200;
            res.data = data;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取失败";
        }
        return res;
    }

    /**
     * 获取随机校验码
     * @param response
     * @param key
     * @return
     */
    @GetMapping(value = "/randomImage/{key}")
    @Operation(summary = "获取随机校验码")
    public BaseResponseInfo randomImage(HttpServletResponse response,@PathVariable String key){
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Map<String, Object> data = new HashMap<>();
            String codeNum = Tools.getCharAndNum(4);
            String base64 = RandImageUtil.generate(codeNum);
            data.put("codeNum", codeNum);
            data.put("base64", base64);
            res.code = 200;
            res.data = data;
        } catch (Exception e) {
            e.printStackTrace();
            res.code = 500;
            res.data = "获取失败";
        }
        return res;
    }

    /**
     * 批量设置状态-启用或者禁用
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/batchSetStatus")
    @Operation(summary = "批量设置状态")
    public String batchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request)throws Exception {
        Byte status = jsonObject.getByte("status");
        String ids = jsonObject.getString("ids");
        Map<String, Object> objectMap = new HashMap<>();
        int res = userService.batchSetStatus(status, ids, request);
        if(res > 0) {
            return ResponseJsonUtil.returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return ResponseJsonUtil.returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 获取当前用户的用户数量和租户信息
     * @param request
     * @return
     */
    @GetMapping(value = "/infoWithTenant")
    @Operation(summary = "获取当前用户的用户数量和租户信息")
    public BaseResponseInfo randomImage(HttpServletRequest request){
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Map<String, Object> data = new HashMap<>();
            Long userId = Long.parseLong(redisService.getObjectFromSessionByKey(request,"userId").toString());
            User user = userService.getUser(userId);
            //获取当前用户数
            int userCurrentNum = userService.getUser().size();
            Tenant tenant = tenantService.getTenantByTenantId(user.getTenantId());
            data.put("type", tenant.getType()); //租户类型，0免费租户，1付费租户
            data.put("expireTime", Tools.parseDateToStr(tenant.getExpireTime()));
            data.put("userCurrentNum", userCurrentNum);
            data.put("userNumLimit", tenant.getUserNumLimit());
            data.put("tenantId", tenant.getTenantId());
            res.code = 200;
            res.data = data;
        } catch (Exception e) {
            e.printStackTrace();
            res.code = 500;
            res.data = "获取失败";
        }
        return res;
    }
}
