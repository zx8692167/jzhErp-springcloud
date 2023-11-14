package com.jzh.hystrix.consumer.service;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.AbstractDiscoveryClientOptionalArgs;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.shared.resolver.EndpointRandomizer;
import com.netflix.discovery.shared.transport.jersey.TransportClientFactories;
import jakarta.inject.Singleton;
import org.springframework.stereotype.Component;

//@Component
public class ConsumerDiscoveryClient {}/*extends DiscoveryClient {
    public ConsumerDiscoveryClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig config) {
        super(applicationInfoManager, config,null);
    }

    public ConsumerDiscoveryClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig config, TransportClientFactories transportClientFactories) {
        super(applicationInfoManager, config, transportClientFactories);
    }

    public ConsumerDiscoveryClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig config, TransportClientFactories transportClientFactories, AbstractDiscoveryClientOptionalArgs args) {
        super(applicationInfoManager, config, transportClientFactories, args);
    }

    public ConsumerDiscoveryClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig config, TransportClientFactories transportClientFactories, AbstractDiscoveryClientOptionalArgs args, EndpointRandomizer randomizer) {
        super(applicationInfoManager, config, transportClientFactories, args, randomizer);
    }
}*/
