package com.jzh.erp.securityauthentication;

import com.jzh.erp.controller.UserController;
import com.jzh.erp.datasource.entities.User;
import com.jzh.erp.service.redis.RedisService;
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
import java.security.Principal;
import java.util.Objects;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    //private TokenManager tokenManager; // 工具类，用于生成/解析token。
    private UserController userController;
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
        Principal principal=request.getUserPrincipal();
        if (Objects.isNull(authentication)){
            filterChain.doFilter(request,response);
            return;
        }
        // 存入SecurityContextHolder中
        //SecurityContextHolder.getContext().setAuthentication(authentication);
        Object userId = redisService.getObjectFromSessionByKey(request,"userId");
        //Object userName = redisService.getObjectFromSessionByKey(request,"loginName");
        if(Objects.isNull(userId)&&!authentication.getPrincipal().equals("anonymousUser")){
            UserDetails userDetails = (UserDetails)authentication.getPrincipal();
            User user=new User();
            user.setLoginName(userDetails.getUsername());
            user.setPassword(String.valueOf(request.getSession().getAttribute("passwordMD5")));

            try {
                userController.login(user, request);
            //                   userController.login(user, request); response.addHeader(ACCESS_TOKEN,((Map)baseResponseInfo.data).get("token").toString());

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
