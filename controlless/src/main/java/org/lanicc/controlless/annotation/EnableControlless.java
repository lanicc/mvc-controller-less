package org.lanicc.controlless.annotation;

import org.lanicc.controlless.config.RequestMappingHandlerAdapterConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created on 2021/5/26.
 *
 * @author lan
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(RequestMappingHandlerAdapterConfig.class)
public @interface EnableControlless {
}
