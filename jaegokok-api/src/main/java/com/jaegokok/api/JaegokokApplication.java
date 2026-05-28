package com.jaegokok.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.jaegokok")
public class JaegokokApplication {

    public static void main(String[] args) {
        SpringApplication.run(JaegokokApplication.class, args);
    }
}
