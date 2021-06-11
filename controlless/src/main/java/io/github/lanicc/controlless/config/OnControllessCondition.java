package io.github.lanicc.controlless.config;

import io.github.lanicc.controlless.annotation.EnableControlless;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created on 2021/6/7.
 *
 * @author lan
 * @since 2.0.0
 */
public abstract class OnControllessCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> beansWithAnnotation = Objects.requireNonNull(context.getBeanFactory()).getBeansWithAnnotation(EnableControlless.class);
        Collection<Object> objects = beansWithAnnotation.values();
        return objects.stream()
                .map(Object::getClass)
                .map((Function<Class<?>, EnableControlless>) clazz -> clazz.getDeclaredAnnotation(EnableControlless.class))
                .anyMatch(getPredicate());
    }

    public abstract Predicate<EnableControlless> getPredicate();
}
