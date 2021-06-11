package io.github.lanicc.controlless.config;

import java.util.Map;

/**
 * Created on 2021/6/7.
 *
 * @author lan
 * @since 2.0.0
 */
public abstract class ControllessAbstractRegister {

    public void init() {
    }

    public void register(Map<Class<?>, Object> beanMap) {
        beanMap.forEach(this::register);
    }

    public abstract void register(Class<?> clazz, Object bean);

}
