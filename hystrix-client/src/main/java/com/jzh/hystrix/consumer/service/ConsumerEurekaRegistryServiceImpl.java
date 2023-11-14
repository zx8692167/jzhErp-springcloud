package com.jzh.hystrix.consumer.service;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.config.ConfigurationManager;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import io.seata.common.exception.EurekaRegistryException;
import io.seata.common.util.StringUtils;
import io.seata.config.Configuration;
import io.seata.config.ConfigurationFactory;
import io.seata.discovery.registry.eureka.CustomEurekaInstanceConfig;
import io.seata.discovery.registry.eureka.EurekaRegistryServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

//@Component
public class ConsumerEurekaRegistryServiceImpl {}/*extends EurekaRegistryServiceImpl {
    @Autowired
    private static volatile EurekaClient eurekaClient;
    @Autowired
    private static volatile CustomEurekaInstanceConfig instanceConfig;
    @Autowired
    private static volatile ApplicationInfoManager applicationInfoManager;

    @Autowired
    private static final Configuration FILE_CONFIG;

    static {
        FILE_CONFIG = ConfigurationFactory.CURRENT_FILE_INSTANCE;
    }


    @Resource
    private EurekaClient getEurekaClient(boolean needRegister) throws EurekaRegistryException {
        if (eurekaClient == null) {
            Class var2 = EurekaRegistryServiceImpl.class;
            synchronized(EurekaRegistryServiceImpl.class) {
                try {
                    if (eurekaClient == null) {
                        if (!needRegister) {
                            instanceConfig = new CustomEurekaInstanceConfig();
                        }

                        ConfigurationManager.loadProperties(this.getEurekaProperties(needRegister));
                        InstanceInfo instanceInfo = (new EurekaConfigBasedInstanceInfoProvider(instanceConfig)).get();
                        applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
                        eurekaClient = new DiscoveryClient(applicationInfoManager, new DefaultEurekaClientConfig(),null);
                    }
                } catch (Exception var5) {
                    this.clean();
                    throw new EurekaRegistryException("register eureka is error!", var5);
                }
            }
        }

        return eurekaClient;
    }

    private void clean() {
        eurekaClient = null;
        applicationInfoManager = null;
        instanceConfig = null;
    }
    private String getEurekaServerUrlFileKey() {
        return String.join(".", "registry", "eureka", "serviceUrl");
    }
    private String getEurekaInstanceWeightFileKey() {
        return String.join(".", "registry", "eureka", "weight");
    }
    private Properties getEurekaProperties(boolean needRegister) {
        Properties eurekaProperties = new Properties();
        eurekaProperties.setProperty("eureka.client.refresh.interval", String.valueOf(5));
        String url = FILE_CONFIG.getConfig(this.getEurekaServerUrlFileKey());
        if (StringUtils.isBlank(url)) {
            throw new EurekaRegistryException("eureka server url can not be null!");
        } else {
            eurekaProperties.setProperty("eureka.serviceUrl.default", url);
            String weight = FILE_CONFIG.getConfig(this.getEurekaInstanceWeightFileKey());
            if (StringUtils.isNotBlank(weight)) {
                eurekaProperties.setProperty("eureka.metadata.weight", weight);
            } else {
                eurekaProperties.setProperty("eureka.metadata.weight", "1");
            }

            if (!needRegister) {
                eurekaProperties.setProperty("eureka.registration.enabled", "false");
            }

            return eurekaProperties;
        }
    }
}*/
