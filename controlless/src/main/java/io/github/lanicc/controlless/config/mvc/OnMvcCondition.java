package io.github.lanicc.controlless.config.mvc;

import io.github.lanicc.controlless.annotation.EnableControlless;
import io.github.lanicc.controlless.config.OnControllessCondition;

import java.util.function.Predicate;

/**
 * Created on 2021/6/7.
 *
 * @author lan
 * @since 2.0.0
 */
public class OnMvcCondition extends OnControllessCondition {
    @Override
    public Predicate<EnableControlless> getPredicate() {
        return EnableControlless::mvc;
    }
}
