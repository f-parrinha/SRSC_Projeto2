package com.server.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Repository
public class FServerStorage {
    public static void main(String[] args) {
        SpringApplication.run(FServerStorage.class, args);
    }
}
