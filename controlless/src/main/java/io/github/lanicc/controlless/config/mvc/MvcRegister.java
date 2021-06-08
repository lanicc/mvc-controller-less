package io.github.lanicc.controlless.config.mvc;

import io.github.lanicc.controlless.config.ControllessAbstractRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Created on 2021/6/7.
 *
 * @author lan
 * @since 2.0.0
 */
@Slf4j
public class MvcRegister extends ControllessAbstractRegister {
    private RequestMappingHandlerMapping mapping;

    public MvcRegister(RequestMappingHandlerMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public void register(Class<?> clazz, Object bean) {
        String simpleName = clazz.getSimpleName();
        Class<?>[] interfaces = ClassUtils.getAllInterfacesForClass(clazz);
        Set<String> methodSet = new HashSet<>();
        for (Class<?> itf : interfaces) {
            Method[] methods = itf.getDeclaredMethods();
            for (Method method : methods) {
                if (methodSet.add(method.getName())) {
                    addMapping(bean, simpleName, method);
                }
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
