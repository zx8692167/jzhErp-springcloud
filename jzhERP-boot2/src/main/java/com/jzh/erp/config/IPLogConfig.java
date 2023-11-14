package com.jzh.erp.config;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.jzh.erp.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * Author: lizhi
 * Date: 2021-10-11
 * Describe: 日志输出ip配置
 */
public class IPLogConfig extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return "127.0.0.1";
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        return IpUtils.getIpAddr(request);
    }
}
