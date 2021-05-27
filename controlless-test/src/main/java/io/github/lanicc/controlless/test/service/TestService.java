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
public class TestService {

    public void echo(String s) {
        System.out.println("ssss: " + s);
    }

    public Map<String, String> echo1(Map<String, String> map) {
        System.out.println(map);
        map.put("time", LocalDateTime.now().toString());
        return map;
    }
}
