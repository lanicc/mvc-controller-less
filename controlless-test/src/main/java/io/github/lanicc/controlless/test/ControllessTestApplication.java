package io.github.lanicc.controlless.test;

import io.github.lanicc.controlless.annotation.EnableControlless;
import io.github.lanicc.controlless.config.ServiceExportProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@SpringBootApplication
@EnableControlless(dubbo = true, mvc = true)
public class ControllessTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ControllessTestApplication.class, args);
    }

}
