package io.github.lanicc.controlless.test.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Created on 2021/5/26.
 *
 * @author lan
 * @since 1.0
 */
@Service
public class TestService implements ITestService {

    @Override
    public String call(String name) {
        System.out.println("name: " + name);
        return "call me baby: " + name;
    }

    @Override
    public Map<String, Object> haha(Map<String, Object> map) {
        System.out.println(map);
        map.put("time", LocalDateTime.now().toString());
        return map;
    }

    @Override
    public Map<String, Object> haha2(Map<String, Object> map, String name) {
        System.out.println("name: " + name + ", map: " + map);
        map.put("name", name);
        return map;
    }
}
