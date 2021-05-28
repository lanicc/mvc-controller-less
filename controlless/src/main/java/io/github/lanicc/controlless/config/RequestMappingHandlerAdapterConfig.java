package io.github.lanicc.controlless.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Collection;
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
    public void initHandlerMapping(RequestMappingHandlerMapping mapping, ApplicationContext applicationContext) {
        Map<String, Object> serviceBeans = applicationContext.getBeansWithAnnotation(Service.class);
        Collection<Object> objects = serviceBeans.values();
        ControllessRequestMappingRegister register = new ControllessRequestMappingRegister(mapping);
        register.register(objects);
    }

    @Autowired
    public void initMappingHandlerAdapter(RequestMappingHandlerAdapter requestMappingHandlerAdapter, ApplicationContext applicationContext) {
        ControllessMethodArgumentReturnValueHandler resolverAndReturnValueHandler = new ControllessMethodArgumentReturnValueHandler(requestMappingHandlerAdapter, applicationContext);
        requestMappingHandlerAdapter.setReturnValueHandlers(Collections.singletonList(resolverAndReturnValueHandler));
        requestMappingHandlerAdapter.setArgumentResolvers(Collections.singletonList(resolverAndReturnValueHandler));
    }

}
