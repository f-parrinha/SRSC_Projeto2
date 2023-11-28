package com.server;

import com.api.common.shell.Shell;
import com.api.common.shell.StorePasswords;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class FAccess extends FServer {

    /** Constants */
    public static final int PORT = 8083;
    public static final String KEYSTORE_PATH = "classpath:faccess-ks.jks";
    public static final String KEY_ALIAS = "faccess";
    public static final String TRUSTSTORE_PATH = "classpath:faccess-ts.jks";

    public static void main(String[] args) {
        SpringApplication.run(FAccess.class, args);
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> serverConfig() {
        StorePasswords passwords = Shell.loadTrustKeyStoresPass();
        return createWebServerFactory(PORT, KEYSTORE_PATH, KEY_ALIAS, TRUSTSTORE_PATH, passwords);
    }
}
