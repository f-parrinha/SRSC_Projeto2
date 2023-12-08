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
public class FAuth extends FServer {

    /** Constants */
    public static final int PORT = 8082;
    public static final String KEYSTORE_PATH = "classpath:fauth-ks.jks";
    public static final String KEY_ALIAS = "fauth";
    public static final String TRUSTSTORE_PATH = "classpath:fauth-ts.jks";
    private static String[] args;

    public static void main(String[] args) {
        FAuth.args = args;
        SpringApplication.run(FAuth.class, args);
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> serverConfig() {
        StorePasswords passwords = Shell.loadTrustKeyStoresPass(args);
        return createWebServerFactory(PORT, KEYSTORE_PATH, KEY_ALIAS, TRUSTSTORE_PATH, passwords);
    }
}
