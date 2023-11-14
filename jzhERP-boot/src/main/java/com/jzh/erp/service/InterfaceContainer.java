package com.jzh.erp.service;

import com.jzh.erp.service.redis.RedisService;
import com.jzh.erp.utils.AnnotationUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jishenghua 2018-10-7 15:25:09
 */
@Service
public class InterfaceContainer {
    private final Map<String, ICommonQuery> configComponentMap = new HashMap<>();

    @Resource
    RedisService redisService;

    @Autowired(required = false)
    private synchronized void init(ICommonQuery[] configComponents) {
        for (ICommonQuery configComponent : configComponents) {
            ResourceInfo info = AnnotationUtils.getAnnotation(configComponent, ResourceInfo.class);
            if (info != null) {
                configComponentMap.put(info.value(), configComponent);
            }
        }
        //System.out.println(configComponentMap.keySet().toString());
        redisService.storageObjectBySession("configComponentMap","jzhERP-boot",configComponentMap.keySet().toString());
    }

    public ICommonQuery getCommonQuery(String apiName) {
        return configComponentMap.get(apiName);
    }
}
