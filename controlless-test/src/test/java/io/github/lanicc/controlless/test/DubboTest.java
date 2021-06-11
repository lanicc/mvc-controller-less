package io.github.lanicc.controlless.test;

import io.github.lanicc.controlless.test.service.ITestService;
import io.github.lanicc.controlless.test.service.TestService;
import org.apache.dubbo.config.*;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Created on 2021/6/7.
 *
 * @author lan
 * @since 2.0.0
 */
public class DubboTest {

    @Test
    void register() throws InterruptedException {
        ITestService testService = new TestService();

        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("lanicc");

        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("zookeeper://127.0.0.1:2181");
        registryConfig.setUsername("lanicc");
        registryConfig.setPassword("lanicc");

        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setName("dubbo");
        protocol.setPort(12345);
        protocol.setThreads(10);

        ServiceConfig<ITestService> serviceConfig = new ServiceConfig<>();
        serviceConfig.setApplication(applicationConfig);
        serviceConfig.setRegistry(registryConfig);
        serviceConfig.setProtocol(protocol);
        serviceConfig.setInterface(ITestService.class);
        serviceConfig.setRef(testService);
        serviceConfig.setVersion("1.0.0");

        serviceConfig.export();

        new CountDownLatch(1).await();
    }

    @Test
    void consumer() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("lanicc");

        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("zookeeper://127.0.0.1:2181");

        ReferenceConfig<ITestService> reference = new ReferenceConfig<>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        reference.setApplication(applicationConfig);
        reference.setRegistry(registryConfig);
        reference.setInterface(ITestService.class);
        reference.setVersion("1.0.0");

        ITestService testService = reference.get();
        String asda = testService.call("asda");
        System.out.println(asda);
    }
}
