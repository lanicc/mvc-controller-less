package io.github.lanicc.controlless.config;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.lang.reflect.Executable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created on 2021/5/26.
 *
 * @author lan
 * @since 1.0
 */
public class ControllessMethodArgumentReturnValueHandler implements HandlerMethodArgumentResolver, HandlerMethodReturnValueHandler {

    private final List<HandlerMethodReturnValueHandler> returnValueHandlers;
    private final List<HandlerMethodArgumentResolver> argumentResolvers;
    private final ServiceRequestResponseBodyMethodProcessor serviceRequestResponseBodyMethodProcessor;

    private final ApplicationContext applicationContext;

    public ControllessMethodArgumentReturnValueHandler(RequestMappingHandlerAdapter requestMappingHandlerAdapter, ApplicationContext applicationContext) {
        this.returnValueHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();
        this.argumentResolvers = requestMappingHandlerAdapter.getArgumentResolvers();
        this.serviceRequestResponseBodyMethodProcessor = new ServiceRequestResponseBodyMethodProcessor(requestMappingHandlerAdapter.getMessageConverters());
        this.applicationContext = applicationContext;
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
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Service.class);
        Class<?> declaringClass = executable.getDeclaringClass();
        Map<String, ?> beansOfType = applicationContext.getBeansOfType(declaringClass);
        HashSet<?> objects = new HashSet<>(beansOfType.values());
        return beans.values().stream()
                .anyMatch(objects::contains);
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

    static class ServiceRequestResponseBodyMethodProcessor extends RequestResponseBodyMethodProcessor {

        public ServiceRequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters) {
            super(converters);
        }

        @Override
        protected boolean checkRequired(MethodParameter parameter) {
            return true;
        }
    }

}
