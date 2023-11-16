package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public abstract class FServer {

    public static void main(String[] args) {
        SpringApplication.run(FServer.class, args);
    }
}

