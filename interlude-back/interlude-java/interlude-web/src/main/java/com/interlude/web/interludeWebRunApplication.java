package com.interlude.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.interlude"})
@MapperScan(basePackages = {"com.interlude.mapper"})
public class interludeWebRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(interludeWebRunApplication.class, args);
    }
}
