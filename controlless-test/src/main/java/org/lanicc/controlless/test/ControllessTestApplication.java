package org.lanicc.controlless.test;

import org.lanicc.controlless.annotation.EnableControlless;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableControlless
public class ControllessTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ControllessTestApplication.class, args);
    }

}
