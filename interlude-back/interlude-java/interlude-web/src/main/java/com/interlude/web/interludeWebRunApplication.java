package com.interlude.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"com.interlude"},exclude = {DataSourceAutoConfiguration.class})
public class interludeWebRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(interludeWebRunApplication.class, args);
    }
}
