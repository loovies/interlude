package com.interlude.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"com.interlude"})
@MapperScan(basePackages = {"com.interlude.mapper"})
public class interludeAdminRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(interludeAdminRunApplication.class, args);
    }
}

