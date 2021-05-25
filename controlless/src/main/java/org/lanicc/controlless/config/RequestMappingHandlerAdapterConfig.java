package org.lanicc.controlless.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 不支持同一个service中有重名的方法
 * Created on 2021/5/25.
 *
 * @author lan
 * @since 1.0
 */
@Configuration
@Order
@Slf4j
public class RequestMappingHandlerAdapterConfig {

    @Autowired
    public void initHandlerMapping(RequestMappingHandlerMapping mapping, ApplicationContext applicationContext) {
        Map<String, Object> serviceBeans = applicationContext.getBeansWithAnnotation(Service.class);
        serviceBeans.values().forEach(bean -> addMapping(mapping, bean));
    }

    @Autowired
    public void initMappingHandlerAdapter(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        ServiceMethodArgumentResolverAndReturnValueHandler resolverAndReturnValueHandler = new ServiceMethodArgumentResolverAndReturnValueHandler(requestMappingHandlerAdapter);
        requestMappingHandlerAdapter.setReturnValueHandlers(Collections.singletonList(resolverAndReturnValueHandler));
        requestMappingHandlerAdapter.setArgumentResolvers(Collections.singletonList(resolverAndReturnValueHandler));
    }


    private void addMapping(RequestMappingHandlerMapping mapping, Object bean) {
        Class<?> beanClass = bean.getClass();
        String simpleName = beanClass.getSimpleName();
        Stream.of(beanClass.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .forEach(method -> addMapping(mapping, bean, simpleName, method));
    }

    private void addMapping(RequestMappingHandlerMapping mapping, Object bean, String className, Method method) {
        String name = method.getName();
        RequestMappingInfo info =
                RequestMappingInfo
                        .paths("/" + className + "/" + name)
                        .methods(RequestMethod.GET, RequestMethod.POST)
                        .build();
        mapping.registerMapping(info, bean, method);
        log.info("bean: {} register method: {}, RequestMappingInfo: {}", bean.getClass(), method.getName(), info);
    }

    static class ServiceRequestResponseBodyMethodProcessor extends RequestResponseBodyMethodProcessor {

        public ServiceRequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters) {
            super(converters);
        }

        @Override
        protected boolean checkRequired(MethodParameter parameter) {
            return true;
        }
    }


    static class ServiceMethodArgumentResolverAndReturnValueHandler implements HandlerMethodArgumentResolver, HandlerMethodReturnValueHandler {

        private final List<HandlerMethodReturnValueHandler> returnValueHandlers;
        private final List<HandlerMethodArgumentResolver> argumentResolvers;
        private final ServiceRequestResponseBodyMethodProcessor serviceRequestResponseBodyMethodProcessor;

        public ServiceMethodArgumentResolverAndReturnValueHandler(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
            this.returnValueHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();
            this.argumentResolvers = requestMappingHandlerAdapter.getArgumentResolvers();
            this.serviceRequestResponseBodyMethodProcessor = new ServiceRequestResponseBodyMethodProcessor(requestMappingHandlerAdapter.getMessageConverters());
        }

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return isService(parameter.getExecutable()) || argumentResolvers.stream().anyMatch(handlerMethodArgumentResolver -> handlerMethodArgumentResolver.supportsParameter(parameter));
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
            if (isService(parameter.getExecutable())) {
                if (BeanUtils.isSimpleProperty(parameter.getNestedParameterType())) {
                    return argumentResolvers.stream()
                            .filter(handlerMethodArgumentResolver -> handlerMethodArgumentResolver instanceof RequestParamMethodArgumentResolver)
                            .findFirst()
                            .map(handlerMethodArgumentResolver -> resolveArgument(handlerMethodArgumentResolver, parameter, mavContainer, webRequest, binderFactory))
                            .orElse(null);

                }
                return resolveArgument(serviceRequestResponseBodyMethodProcessor, parameter, mavContainer, webRequest, binderFactory);
            }
            return argumentResolvers.stream()
                    .filter(handlerMethodArgumentResolver -> handlerMethodArgumentResolver.supportsParameter(parameter))
                    .findFirst()
                    .map(handlerMethodArgumentResolver -> resolveArgument(handlerMethodArgumentResolver, parameter, mavContainer, webRequest, binderFactory))
                    .orElse(null);
        }

        @Override
        public boolean supportsReturnType(MethodParameter returnType) {
            return isService(returnType.getExecutable()) || returnValueHandlers.stream().anyMatch(returnValueHandler -> returnValueHandler.supportsReturnType(returnType));
        }

        @Override
        public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
            if (isService(returnType.getExecutable())) {
                handleReturnValue(serviceRequestResponseBodyMethodProcessor, returnValue, returnType, mavContainer, webRequest);
            } else {
                returnValueHandlers.stream()
                        .filter(returnValueHandler -> returnValueHandler.supportsReturnType(returnType))
                        .findFirst()
                        .ifPresent(returnValueHandler -> handleReturnValue(returnValueHandler, returnValue, returnType, mavContainer, webRequest));
            }
        }

        private boolean isService(Executable executable) {
            return executable.getDeclaringClass().isAnnotationPresent(Service.class);
        }

        private Object resolveArgument(HandlerMethodArgumentResolver handlerMethodArgumentResolver, MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
            try {
                return handlerMethodArgumentResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void handleReturnValue(HandlerMethodReturnValueHandler returnValueHandler, Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) {
            try {
                returnValueHandler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

}
