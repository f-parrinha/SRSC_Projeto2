package com.server;

import com.api.common.shell.Shell;
import com.api.common.shell.StorePasswords;
import com.api.common.tls.TLSConfigFactory;
import com.api.common.tls.TLSServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;

import java.io.FileNotFoundException;
import java.io.InputStream;

public abstract class FServer {
    private final InputStream CONFIG_FILE = FServer.class.getClassLoader().getResourceAsStream("servertls.conf");


    protected WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> createWebServerFactory(int port, String keyStorePath, String keyAlias, String trustStorePath) {
        return factory -> {
            try {
                StorePasswords passwords = Shell.loadTrustKeyStoresPass();
                TLSServerConfig tls = TLSConfigFactory.getInstance().forServer()
                        .withConfigFile(CONFIG_FILE)
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
