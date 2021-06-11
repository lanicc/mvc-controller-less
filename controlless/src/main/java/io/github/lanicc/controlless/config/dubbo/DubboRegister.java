package io.github.lanicc.controlless.config.dubbo;

import io.github.lanicc.controlless.config.ControllessAbstractRegister;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.springframework.util.ClassUtils;

/**
 * Created on 2021/6/7.
 *
 * @author lan
 * @since 2.0.0
 */
@Slf4j
public class DubboRegister extends ControllessAbstractRegister {

    @Setter
    private String application;

    @Setter
    private String address;

    @Setter
    private Integer port;

    private ApplicationConfig applicationConfig;

    private RegistryConfig registryConfig;

    private ProtocolConfig protocolConfig;

    @Override
    public void init() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(this.application);
        this.applicationConfig = applicationConfig;

        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(this.address);
        this.registryConfig = registryConfig;

        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("dubbo");
        protocolConfig.setPort(port);
        protocolConfig.setThreads(10);
        this.protocolConfig = protocolConfig;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void register(Class<?> clazz, Object bean) {
        Class<?>[] interfaces = ClassUtils.getAllInterfacesForClass(clazz);
        for (Class<?> itf : interfaces) {
            ServiceConfig serviceConfig = new ServiceConfig();
            serviceConfig.setApplication(this.applicationConfig);
            serviceConfig.setRegistry(registryConfig);
            serviceConfig.setProtocol(protocolConfig);
            serviceConfig.setInterface(itf);
            serviceConfig.setRef(bean);
            serviceConfig.setVersion("1.0.0");

            serviceConfig.export();
            log.info("Service {} export.", clazz);
        }

    }
}
