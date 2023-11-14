package io.seata.discovery.registry.eureka;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.config.ConfigurationManager;
import com.netflix.discovery.*;
import com.netflix.discovery.shared.Application;
//import com.netflix.discovery.shared.transport.jersey.Jersey1TransportClientFactories;
import com.netflix.discovery.shared.transport.jersey.TransportClientFactories;
//import com.netflix.discovery.shared.transport.jersey3.Jersey3TransportClientFactories;
import io.seata.common.exception.EurekaRegistryException;
import io.seata.common.util.CollectionUtils;
import io.seata.common.util.NetUtil;
import io.seata.common.util.StringUtils;
import io.seata.config.Configuration;
import io.seata.config.ConfigurationFactory;
//import io.seata.config.exception.ConfigNotFoundException;
import io.seata.discovery.registry.RegistryService;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.CloudEurekaClient;
import org.springframework.cloud.netflix.eureka.CloudEurekaTransportConfig;
import org.springframework.cloud.netflix.eureka.http.DefaultEurekaClientHttpRequestFactorySupplier;
import org.springframework.cloud.netflix.eureka.http.RestTemplateDiscoveryClientOptionalArgs;
import org.springframework.cloud.netflix.eureka.http.RestTemplateTransportClientFactories;
import org.springframework.cloud.netflix.eureka.http.WebClientTransportClientFactories;
import org.springframework.stereotype.Component;

@Component
//@org.springframework.context.annotation.Configuration
//@Configuration(proxyBeanMethods = false)
public class EurekaRegistryServiceImpl implements RegistryService<EurekaEventListener> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EurekaRegistryServiceImpl.class);
    private static final String DEFAULT_APPLICATION = "default";
    private static final String PRO_SERVICE_URL_KEY = "serviceUrl";
    private static final String FILE_ROOT_REGISTRY = "registry";
    private static final String FILE_CONFIG_SPLIT_CHAR = ".";
    private static final String REGISTRY_TYPE = "eureka";
    private static final String CLUSTER = "application";
    private static final String REGISTRY_WEIGHT = "weight";
    private static final String EUREKA_CONFIG_SERVER_URL_KEY = "eureka.serviceUrl.default";
    private static final String EUREKA_CONFIG_REFRESH_KEY = "eureka.client.refresh.interval";
    private static final String EUREKA_CONFIG_SHOULD_REGISTER = "eureka.registration.enabled";
    private static final String EUREKA_CONFIG_METADATA_WEIGHT = "eureka.metadata.weight";
    private static final int EUREKA_REFRESH_INTERVAL = 5;
    private static final int MAP_INITIAL_CAPACITY = 8;
    private static final String DEFAULT_WEIGHT = "1";
    private static final Configuration FILE_CONFIG;
    private static final ConcurrentMap<String, List<EurekaEventListener>> LISTENER_SERVICE_MAP;
    private static final ConcurrentMap<String, List<InetSocketAddress>> CLUSTER_ADDRESS_MAP;
    private static final ConcurrentMap<String, Object> CLUSTER_LOCK;
    private static volatile ApplicationInfoManager applicationInfoManager;
    private static volatile CustomEurekaInstanceConfig instanceConfig;
    private static volatile EurekaRegistryServiceImpl instance;
    private static volatile EurekaClient eurekaClient;


    @Autowired
    AbstractDiscoveryClientOptionalArgs abstractDiscoveryClientOptionalArgs;
    @Autowired
    RestTemplateTransportClientFactories restTemplateTransportClientFactories;

    @Autowired
    WebClientTransportClientFactories webClientTransportClientFactories;

    @Autowired
    private AbstractDiscoveryClientOptionalArgs<?> optionalArgs;
    /*@Autowired
    Jersey3TransportClientFactories jersey3TransportClientFactories;*/

    @Autowired
    TransportClientFactories transportClientFactories;

    @Autowired
    CloudEurekaClient client;

    private EurekaRegistryServiceImpl() {
    }

    static EurekaRegistryServiceImpl getInstance() {
        if (instance == null) {
            Class var0 = EurekaRegistryServiceImpl.class;
            synchronized(EurekaRegistryServiceImpl.class) {
                if (instance == null) {
                    instanceConfig = new CustomEurekaInstanceConfig();
                    instance = new EurekaRegistryServiceImpl();
                }
            }
        }

        return instance;
    }

    public void register(InetSocketAddress address) throws Exception {
        NetUtil.validAddress(address);
        instanceConfig.setIpAddress(address.getAddress().getHostAddress());
        instanceConfig.setPort(address.getPort());
        instanceConfig.setApplicationName(this.getApplicationName());
        instanceConfig.setInstanceId(this.getInstanceId());
        this.getEurekaClient(true);
        applicationInfoManager.setInstanceStatus(InstanceStatus.UP);
    }

    public void unregister(InetSocketAddress address) throws Exception {
        if (eurekaClient != null) {
            applicationInfoManager.setInstanceStatus(InstanceStatus.DOWN);
        }
    }

    public void subscribe(String cluster, EurekaEventListener listener) throws Exception {
        ((List)LISTENER_SERVICE_MAP.computeIfAbsent(cluster, (key) -> {
            return new ArrayList();
        })).add(listener);
        this.getEurekaClient(false).registerEventListener(listener);
    }

    public void unsubscribe(String cluster, EurekaEventListener listener) throws Exception {
        List<EurekaEventListener> subscribeList = (List)LISTENER_SERVICE_MAP.get(cluster);
        if (subscribeList != null) {
            List<EurekaEventListener> newSubscribeList = (List)subscribeList.stream().filter((eventListener) -> {
                return !eventListener.equals(listener);
            }).collect(Collectors.toList());
            LISTENER_SERVICE_MAP.put(cluster, newSubscribeList);
        }

        this.getEurekaClient(false).unregisterEventListener(listener);
    }

    public List<InetSocketAddress> lookup(String key) throws Exception {
        String clusterName = this.getServiceGroup(key);
        String clusterUpperName;
        if (clusterName == null) {
            clusterUpperName = "service.vgroupMapping." + key;
            throw new  Exception("%s configuration item is required"+clusterUpperName, null);

            //    throw new ConfigNotFoundException("%s configuration item is required", new String[]{clusterUpperName});
        } else {
            clusterUpperName = clusterName.toUpperCase();
            if (!LISTENER_SERVICE_MAP.containsKey(clusterUpperName)) {
                Object lock = CLUSTER_LOCK.computeIfAbsent(clusterUpperName, (k) -> {
                    return new Object();
                });
                synchronized(lock) {
                    if (!LISTENER_SERVICE_MAP.containsKey(clusterUpperName)) {
                        this.refreshCluster(clusterUpperName);
                        this.subscribe(clusterUpperName, (event) -> {
                            this.refreshCluster(clusterUpperName);
                        });
                    }
                }
            }

            return (List)CLUSTER_ADDRESS_MAP.get(clusterUpperName);
        }
    }

    public void close() throws Exception {
        if (eurekaClient != null) {
            eurekaClient.shutdown();
        }

        this.clean();
    }

    private void refreshCluster(String clusterName) {
        Application application = this.getEurekaClient(false).getApplication(clusterName);
        if (application != null && !CollectionUtils.isEmpty(application.getInstances())) {
            List<InetSocketAddress> newAddressList = (List)application.getInstances().stream().filter((instance) -> {
                return InstanceStatus.UP.equals(instance.getStatus()) && instance.getIPAddr() != null && instance.getPort() > 0 && instance.getPort() < 65535;
            }).map((instance) -> {
                return new InetSocketAddress(instance.getIPAddr(), instance.getPort());
            }).collect(Collectors.toList());
            CLUSTER_ADDRESS_MAP.put(clusterName, newAddressList);
        } else {
            LOGGER.info("refresh cluster success,but cluster empty! cluster name:{}", clusterName);
        }

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

    private String getApplicationName() {
        String application = FILE_CONFIG.getConfig(this.getEurekaApplicationFileKey());
        if (application == null) {
            application = "default";
        }

        return application;
    }

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
                        CloudEurekaTransportConfig cloudEurekaClient=new CloudEurekaTransportConfig();
                        //new CloudEurekaClient();
                        //Jersey3ApplicationClientFactory
                        //Jersey3ApplicationClientFactoryBuilder
                        RestTemplateTransportClientFactories restTemplateTransportClientFactories1=new RestTemplateTransportClientFactories(new RestTemplateDiscoveryClientOptionalArgs(new DefaultEurekaClientHttpRequestFactorySupplier()));
                        eurekaClient = new DiscoveryClient(applicationInfoManager, new DefaultEurekaClientConfig(),restTemplateTransportClientFactories1);
                        //org.springframework.cloud.netflix.eureka.CloudEurekaClient.getApplications()
                        //org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration.getEurekaClient()
                        //new EurekaTransport();
                        //eurekaClient = new CloudEurekaClient();

                        //cloudEurekaClient.get
                        //CloudEurekaClient client=new CloudEurekaClient();
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

    private String getInstanceId() {
        return String.format("%s:%s:%d", instanceConfig.getIpAddress(), instanceConfig.getAppname(), instanceConfig.getNonSecurePort());
    }

    private String getEurekaServerUrlFileKey() {
        return String.join(".", "registry", "eureka", "serviceUrl");
    }

    private String getEurekaApplicationFileKey() {
        return String.join(".", "registry", "eureka", "application");
    }

    private String getEurekaInstanceWeightFileKey() {
        return String.join(".", "registry", "eureka", "weight");
    }

    static {
        FILE_CONFIG = ConfigurationFactory.CURRENT_FILE_INSTANCE;
        LISTENER_SERVICE_MAP = new ConcurrentHashMap();
        CLUSTER_ADDRESS_MAP = new ConcurrentHashMap();
        CLUSTER_LOCK = new ConcurrentHashMap();
    }
}

