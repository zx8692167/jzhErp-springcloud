package com.jzh.hystrix.consumer.securityauthentication;

import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.datasource.entities.User;
import com.jzh.erp.utils.BaseResponseInfo;
import com.jzh.hystrix.consumer.controller.UserCenterController;
import com.jzh.hystrix.consumer.service.SystemCenterService;
import com.jzh.hystrix.consumer.service.redis.RedisService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

import static com.jzh.hystrix.consumer.service.redis.RedisService.ACCESS_TOKEN;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    //private TokenManager tokenManager; // 工具类，用于生成/解析token。
    private UserCenterController userCenterController;

    @Resource
    private SystemCenterService systemCenterService;

    @Resource
    private RedisService redisService;

    /**
     *  redis 失效时间跟 session会话失效时间不一致  (token  失效时间)
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 将用户信息封装authentication对象
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        //UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        if (Objects.isNull(authentication)){
            filterChain.doFilter(request,response);
            return;
        }
        // 存入SecurityContextHolder中
        //SecurityContextHolder.getContext().setAuthentication(authentication);
        Object userId = redisService.getObjectFromSessionByKey(request,"userId");
        /*if(userId!=null){
            long oh=Long.valueOf(userId.toString());
            System.out.println("oh "+oh);
        }*/
        //Object userName = redisService.getObjectFromSessionByKey(request,"loginName");
        if(Objects.isNull(userId)&&!authentication.getPrincipal().equals("anonymousUser")){
            UserDetails userDetails = (UserDetails)authentication.getPrincipal();
            User user=new User();
            user.setLoginName(userDetails.getUsername());
            user.setPassword(String.valueOf(request.getSession().getAttribute("passwordMD5")));

            try {
                BaseResponseInfo baseResponseInfo= userCenterController.login(user, request);
            //                   userController.login(user, request); response.addHeader(ACCESS_TOKEN,((Map)baseResponseInfo.data).get("token").toString());
                if(baseResponseInfo.data!=null){
                    Map map=(Map)baseResponseInfo.data;
                    System.out.println("BaseResponseInfo"+" "+map.get("token"));
                    System.out.println("BaseResponseInfo"+" "+request.getSession().getAttribute(ACCESS_TOKEN));

                    request.getSession().setAttribute(ACCESS_TOKEN,map.get("token"));
                    request.setAttribute(ACCESS_TOKEN,map.get("token"));

                    /*boolean forceFlag=false;
                    boolean cusFlag=false;*/
                    if(map.get("token")!=null) {
                        BaseResponseInfo systemCurrentInfo = systemCenterService.getCurrentInfo();
                        if (systemCurrentInfo.code == 200 && systemCurrentInfo.data != null) {
                            // SystemConfig systemConfig = JSON.parseObject(JSON.toJSONString(systemCurrentInfo.data), SystemConfig.class);
                            // String flag = systemConfig.getCustomerFlag();
                        /*String forceApprovalFlag = ((Map)systemCurrentInfo.data).get("forceApprovalFlag").toString();
                        String customerFlag = ((Map)systemCurrentInfo.data).get("forceApprovalFlag").toString();

                        if(("1").equals(forceApprovalFlag)) {
                            forceFlag = true;
                        }
                        if(("1").equals(customerFlag)) {
                            cusFlag = true;
                        }
                        Map<String,Object> systemconfig= new HashMap<String,Object>();
                        systemconfig.put("forceFlag",forceFlag);
                        systemconfig.put("cusFlagy",cusFlag);
                        String ss= JSONObject.toJSONString(systemconfig);
                        System.out.println(ss);*/
                            redisService.storageObjectBySession(map.get("token").toString(), "systemConfig", JSONObject.toJSONString(systemCurrentInfo.data));
                        /*redisService.deleteObjectBySession(request,"forceFlag",ACCESS_SYSTEMCONFIG_TOKEN);
                        String token_systemconfig = UUID.randomUUID().toString().replaceAll("-", "") + "";
                        token_systemconfig = token_systemconfig + "_" + user.getTenantId();
                        token_systemconfig = token_systemconfig + "_" + user.getId();
                        redisService.storageObjectBySession(token_systemconfig,"tenantId",user.getTenantId());
                        redisService.storageObjectBySession(token_systemconfig,"userId",user.getId());
                        redisService.storageObjectBySession(token_systemconfig,"loginName",user.getLoginName());
                        redisService.storageObjectBySession(token_systemconfig,"forceFlag",forceFlag);
                        request.getSession().setAttribute(ACCESS_SYSTEMCONFIG_TOKEN,token_systemconfig);
                        request.setAttribute(ACCESS_SYSTEMCONFIG_TOKEN,token_systemconfig);*/
                        }
                    }
                    /*if(map.get("user")!=null) {

                        BaseResponseInfo basicDataInfo = userCenterController.getBasicData(((Map<String, Object>) map.get("user")).get("id").toString(),null,request);
                        Object userBusinessList=((Map)(basicDataInfo.data)).get("userBusinessList");
                        if(userBusinessList!=null){
                            List list=(ArrayList)userBusinessList;
                            Map<String,Object> basicDataConfig= new HashMap<String,Object>();
                            for (Object userBusiness:list) {
                                basicDataConfig.put(((Map)userBusiness).get("type").toString(),((Map)userBusiness).get("value") );
                            }
                            redisService.storageObjectBySession(map.get("token").toString(),"basicDataConfig",JSONObject.toJSONString(basicDataConfig));
                        }
                    }*/
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        // 放行
        filterChain.doFilter(request, response);
    }

/*    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // 从请求头中获取token值
        String token = request.getHeader("token");
        if (!StringUtils.isEmpty(token)) {
            try {
                // 解析token得到用户名
                *//*String userName = tokenManager.getUserNameFromToken(token);
                userController.login(,request) userName *//*
                return new UsernamePasswordAuthenticationToken(null, token, null); // TODO 权限没整合，所以这里返回null
            } catch (Exception e) {
                throw new RuntimeException("token不合法!");
            }
        }
        return null;
    }*/
}
