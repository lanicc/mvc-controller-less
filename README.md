# mvc-controller-less
不需要写controller，直接暴露service为http接口

## 使用

### 添加maven依赖

````xml

<dependency>
    <groupId>io.github.lanicc</groupId>
    <artifactId>controlless-springboot-starter</artifactId>
    <version>1.0.1</version>
</dependency>

````


### 注解开启

````java

@EnableControlless
@SpringBootApplication
public class XxApplication {

    public static void main(String[] args) {
        SpringApplication.run(XxApplication.class, args);
    }

}

````
