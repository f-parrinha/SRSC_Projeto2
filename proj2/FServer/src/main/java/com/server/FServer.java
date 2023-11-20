package com.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
public class FServer {
//    private final FAuthClient authClient;
//    private final FAccessControlClient accessControlClient;
//    private final FStorageClient fStorageClient;
//
//    public FServer(FAuthClient authClient, FAccessControlClient accessControlClient, FStorageClient fStorageClient) {
//        this.authClient = authClient;
//        this.accessControlClient = accessControlClient;
//        this.fStorageClient = fStorageClient;
//    }

    public static void main(String[] args) {
        SpringApplication.run(FServer.class, args);
    }

    @GetMapping("/ls/{username}")
    public Mono<ResponseEntity<String>> listFiles(@PathVariable String username) {
        return Mono.just("Testing: " + username)
                .map(response -> ResponseEntity.ok("Testing: " + username));
    }
}
