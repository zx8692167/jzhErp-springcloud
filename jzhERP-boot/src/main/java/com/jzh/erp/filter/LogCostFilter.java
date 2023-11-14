package com.jzh.erp.filter;

import com.jzh.erp.securityauthentication.MultiReadHttpServletRequest;
import com.jzh.erp.securityauthentication.MultiReadHttpServletResponse;
import com.jzh.erp.service.redis.RedisService;

import com.jzh.erp.utils.Constants;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@WebFilter(filterName = "LogCostFilter", urlPatterns = {"/*"},
        initParams = {@WebInitParam(name = "filterPath",
                      value = "/jzhERP-boot/user/login#/jzhERP-boot/user/registerUser#/jzhERP-boot/user/randomImage#" +
                              "/jzhERP-boot/platformConfig/getPlatform#/jzhERP-boot/v2/api-docs#/jzhERP-boot/webjars#" +
                              "/jzhERP-boot/systemConfig/static#/jzhERP-boot/api/plugin/wechat/weChat/share")})
public class LogCostFilter implements Filter {
    private Logger logger = LoggerFactory.getLogger(LogCostFilter.class);

    private static final String FILTER_PATH = "filterPath";

    private String[] allowUrls;
    @Resource
    private RedisService redisService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String filterPath = filterConfig.getInitParameter(FILTER_PATH);
        if (!StringUtils.isEmpty(filterPath)) {
            allowUrls = filterPath.contains("#") ? filterPath.split("#") : new String[]{filterPath};
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        //System.out.println("进入 doFilter");

            long startTime = System.currentTimeMillis();
            long endTime = 0;
            endTime = System.currentTimeMillis();

            HttpServletRequest servletRequest = (HttpServletRequest) request;
            HttpServletResponse servletResponse = (HttpServletResponse) response;

            MultiReadHttpServletRequest wrappedRequest = new MultiReadHttpServletRequest(servletRequest);
            MultiReadHttpServletResponse wrappedResponse = new MultiReadHttpServletResponse(servletResponse);
        try {

            // 记录请求的消息体
            logRequestBody(wrappedRequest);

            String requestUrl = servletRequest.getRequestURI();
            //具体，比如：处理若用户未登录，则跳转到登录页
            Object userId = redisService.getObjectFromSessionByKey(servletRequest, "userId");
            if (userId != null) { //如果已登录，不阻止
                chain.doFilter(wrappedRequest, response);
                return;
            }

            //
            if (requestUrl != null && (requestUrl.contains("/doc.html") || requestUrl.contains("/swagger-")
                    || requestUrl.contains("/api-docs") || requestUrl.contains(".jpg") || requestUrl.contains("/login") ||
                    requestUrl.contains("/user/login") || requestUrl.contains("/user/register"))) {
                chain.doFilter(wrappedRequest, response);
                return;
            }
            if (null != allowUrls && allowUrls.length > 0) {
                for (String url : allowUrls) {
                    if (requestUrl.startsWith(url)) {
                        chain.doFilter(wrappedRequest, response);
                        return;
                    }
                }
            }
            //无论有没登录都不拦截
            if (userId == null) {
                chain.doFilter(wrappedRequest, response);
                return;
            }
            servletResponse.setStatus(500);
            if (requestUrl != null && !requestUrl.contains("/user/logout") && !requestUrl.contains("/function/findMenuByPNumber")) {
                servletResponse.getWriter().write("loginOut");
            }
        }catch (Exception e){
            // 记录响应的消息体
            e.printStackTrace();
        }finally {
            logResponseBody(wrappedRequest, wrappedResponse, endTime - startTime);

        }
    }

    @Override
    public void destroy() {

    }




    private String logRequestBody(MultiReadHttpServletRequest request) {
        MultiReadHttpServletRequest wrapper = request;
        if (wrapper != null) {
            try {
                String bodyJson = wrapper.getBodyJsonStrByJson(request);
                String url = wrapper.getRequestURI().replace("//", "/");
                System.out.println("-------------------------------- 请求url: " + url + " --------------------------------");
                Constants.URL_MAPPING_MAP.put(url, url);
                logger.info("`{}` 接收到的参数: {}",url , bodyJson);
                return bodyJson;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void logResponseBody(MultiReadHttpServletRequest request, MultiReadHttpServletResponse response, long useTime) {
        MultiReadHttpServletResponse wrapper = response;
        if (wrapper != null) {
            byte[] buf = wrapper.getBody();
            if (buf.length > 0) {
                String payload;
                try {
                    payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                    payload = "[unknown]";
                }
                logger.info("`{}`  耗时:{}ms  返回的参数: {}", Constants.URL_MAPPING_MAP.get(request.getRequestURI()), useTime, payload);
            }
        }
    }
}