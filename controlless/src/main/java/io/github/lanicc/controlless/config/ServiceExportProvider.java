package io.github.lanicc.controlless.config;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2021/6/7.
 *
 * @author lan
 * @since 2.0.0
 */
public class ServiceExportProvider {

    public Map<Class<?>, Object> getService(ApplicationContext applicationContext) {
        Map<String, Object> serviceBeans = applicationContext.getBeansWithAnnotation(Service.class);
        Collection<Object> beans = serviceBeans.values();

        Map<Class<?>, Object> serviceExports = new HashMap<>(beans.size());
        for (Object bean : beans) {
            Class<?> beanClass = AopUtils.getTargetClass(bean);
            serviceExports.put(beanClass, bean);
        }

        return serviceExports;
    }
}
