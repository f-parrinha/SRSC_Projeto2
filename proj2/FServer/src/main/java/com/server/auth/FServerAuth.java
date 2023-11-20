package com.server.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class FServerAuth {
    public static void main(String[] args) {
        SpringApplication.run(FServerAuth.class, args);
    }
}
