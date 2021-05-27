package io.github.lanicc.controlless.config;

import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Created on 2021/5/26.
 *
 * @author lan
 * @since 1.0
 */
public class ControllessRequestMappingRegister {

    private final RequestMappingHandlerMapping mapping;

    public ControllessRequestMappingRegister(RequestMappingHandlerMapping mapping) {
        this.mapping = mapping;
    }

    public void register(Collection<?> mappingBeans) {
        mappingBeans.forEach(this::addMapping);
    }

    private void addMapping(Object bean) {
        Class<?> beanClass = bean.getClass();
        String simpleName = beanClass.getSimpleName();
        Class<?>[] interfaces = ClassUtils.getAllInterfacesForClass(beanClass);
        for (Class<?> itf : interfaces) {
            Method[] methods = itf.getDeclaredMethods();
            for (Method method : methods) {
                addMapping(bean, simpleName, method);
            }
        }
    }

    private void addMapping(Object bean, String className, Method method) {
        String name = method.getName();
        String path = "/" + className + "/" + name;
        RequestMappingInfo info =
                RequestMappingInfo
                        .paths(path)
                        .methods(RequestMethod.GET, RequestMethod.POST)
                        .build();
        mapping.registerMapping(info, bean, method);
        System.err.printf("ControllessRequestMappingRegister register request mapping, bean class : %s, method: %s, path: %s, http method: %s\n", className, name, path, "GET,POST");
    }


}
