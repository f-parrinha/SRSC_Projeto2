package com.server.modules;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class FAccessControl {
    public static void main(String[] args) {
        SpringApplication.run(FAccessControl.class, args);
    }
}
