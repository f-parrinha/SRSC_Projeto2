package com.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class FAccessControl extends FServer {

    /** Constants */
    public static final int PORT = 8084;
    public static final String KEYSTORE_PATH = "classpath:faccess-ks.jks";
    public static final String KEY_ALIAS = "faccess";
    public static final String TRUSTSTORE_PATH = "classpath:faccess-ts.jks";

    public static void main(String[] args) {
        SpringApplication.run(FAccessControl.class, args);
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> serverConfig() {
        return createWebServerFactory(PORT, KEYSTORE_PATH, KEY_ALIAS, TRUSTSTORE_PATH);
    }
}
