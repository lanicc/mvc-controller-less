package io.github.lanicc.controlless.test.service;

import java.util.Map;

/**
 * Created on 2021/5/28.
 *
 * @author lan
 * @since 1.0
 */
public interface ITestService {

    String call(String name);

    Map<String, Object> haha(Map<String, Object> map);

    Map<String, Object> haha2(Map<String, Object> map, String name);
}
