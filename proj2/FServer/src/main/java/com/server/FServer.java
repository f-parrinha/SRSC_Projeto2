package com.server;

import com.api.common.shell.StorePasswords;
import com.api.common.tls.TLSConfigFactory;
import com.api.common.tls.TLSServerConfig;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

public abstract class FServer {

    /** Constants */
    protected static final InputStream SERVER_CONFIG_FILE = FServer.class.getClassLoader().getResourceAsStream("servertls.conf");
    protected static final InputStream CLIENT_CONFIG_FILE = FServer.class.getClassLoader().getResourceAsStream("clienttls.conf");
    protected static final URI AUTH_URL = URI.create("https://localhost:8082");
    protected static final URI ACCESS_URL = URI.create("https://localhost:8083");
    protected static final URI STORAGE_URL = URI.create("https://localhost:8084");

    protected WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> createWebServerFactory(
            int port, String keyStorePath, String keyAlias, String trustStorePath, StorePasswords passwords) {
        return factory -> {
            try {
                TLSServerConfig tls = TLSConfigFactory.getInstance().forServer()
                        .withConfigFile(SERVER_CONFIG_FILE)
                        .withKeyStorePath(keyStorePath)
                        .withKeyStorePass(passwords.keyStorePass())
                        .withKeyAlias(keyAlias)
                        .withKeyPass(passwords.keyStorePass())
                        .withTrustStorePath(trustStorePath)
                        .withTrustStorePass(passwords.trustStorePass())
                        .build();

                factory.setSsl(tls.getSslContext());
                factory.setPort(port);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
