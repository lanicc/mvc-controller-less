package io.github.lanicc.controlless.config;

import io.github.lanicc.controlless.config.dubbo.DubboRegister;
import io.github.lanicc.controlless.config.dubbo.OnDubboCondition;
import io.github.lanicc.controlless.config.mvc.ControllessMethodArgumentReturnValueHandler;
import io.github.lanicc.controlless.config.mvc.MvcRegister;
import io.github.lanicc.controlless.config.mvc.OnMvcCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Collections;
import java.util.Map;

/**
 * 不支持同一个service中有重名的方法
 * Created on 2021/5/25.
 *
 * @author lan
 * @since 1.0
 */
@Configuration
@Order
public class RequestMappingHandlerAdapterConfig {

    @Autowired
    public void register(@Autowired(required = false) Map<String, ControllessAbstractRegister> registerMap, ServiceExportProvider serviceExportProvider, ApplicationContext applicationContext) {
        if (registerMap != null) {
            Map<Class<?>, Object> map = serviceExportProvider.getService(applicationContext);
            for (ControllessAbstractRegister register : registerMap.values()) {
                register.init();
                register.register(map);
            }
        }
    }

    @Bean
    public ServiceExportProvider serviceExportProvider() {
        return new ServiceExportProvider();
    }


    @Autowired
    @Conditional(OnMvcCondition.class)
    public void initMappingHandlerAdapter(RequestMappingHandlerAdapter requestMappingHandlerAdapter, ApplicationContext applicationContext) {
        ControllessMethodArgumentReturnValueHandler resolverAndReturnValueHandler = new ControllessMethodArgumentReturnValueHandler(requestMappingHandlerAdapter, applicationContext);
        requestMappingHandlerAdapter.setReturnValueHandlers(Collections.singletonList(resolverAndReturnValueHandler));
        requestMappingHandlerAdapter.setArgumentResolvers(Collections.singletonList(resolverAndReturnValueHandler));
    }

    @Bean
    @Conditional(OnMvcCondition.class)
    public MvcRegister mvcRegister(@Autowired RequestMappingHandlerMapping mapping) {
        return new MvcRegister(mapping);
    }

    @Bean
    @Conditional(OnDubboCondition.class)
    @ConfigurationProperties(prefix = "controlless.register.dubbo")
    public DubboRegister dubboRegister() {
        return new DubboRegister();
    }

}
