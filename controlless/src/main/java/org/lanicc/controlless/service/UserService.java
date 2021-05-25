package org.lanicc.controlless.service;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Created on 2021/5/25.
 *
 * @author lan
 * @since 1.0
 */
@ConditionalOnExpression("${openTest}")
@Service
public class UserService {

    public String getTime() {
        return LocalDateTime.now().toString();
    }

    public User getUserById(Integer id) {
        return new User()
                .setAge(id)
                .setName("User-" + id);
    }

    public User echo1(User user) {
        System.out.println(user);
        return user;
    }

    public User echo2(String name, User user) {
        System.out.println(user);
        System.out.println(name);
        return user.setName(name);
    }

    @Data
    @Accessors(chain = true)
    public static class User {

        private String name;

        private Integer age;
    }

}
