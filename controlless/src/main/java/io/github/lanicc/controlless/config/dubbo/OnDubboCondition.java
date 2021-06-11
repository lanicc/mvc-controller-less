package io.github.lanicc.controlless.config.dubbo;

import io.github.lanicc.controlless.annotation.EnableControlless;
import io.github.lanicc.controlless.config.OnControllessCondition;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Created on 2021/6/7.
 *
 * @author lan
 * @since 2.0.0
 */
public class OnDubboCondition extends OnControllessCondition {

    @Override
    public Predicate<EnableControlless> getPredicate() {
        return EnableControlless::dubbo;
    }
}
