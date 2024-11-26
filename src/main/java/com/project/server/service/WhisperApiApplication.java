package com.project.server.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class WhisperApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhisperApiApplication.class, args);
    }

}