package com.interlude.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = {"com.interlude.mapper"})
@SpringBootApplication(scanBasePackages = "com.interlude")
public class InterludeAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(InterludeAuthApplication.class, args);
    }
}
