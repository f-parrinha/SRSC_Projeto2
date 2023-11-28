package com.server;

import com.api.common.tls.TLSConfigFactory;
import com.api.common.tls.TLSServerConfig;
import com.server.modules.FDispatcher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;

import java.io.FileNotFoundException;

public abstract class FServer {

    public static void main(String[] args) {
        SpringApplication.run(FDispatcher.class, args);
    }

    public abstract WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> serverConfig();

    protected WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> createWebServerFactory(ServerConfigs configs) {
        return factory -> {
            try {
                TLSServerConfig tls = TLSConfigFactory.getInstance().forServer()
                        .withConfigFile(configs.configFile())
                        .withKeyStorePath(configs.keyStorePath())
                        .withKeyStorePass(configs.passwords().keyStorePass())
                        .withKeyAlias(configs.keyAlias())
                        .withKeyPass(configs.passwords().keyStorePass())
                        .withTrustStorePath(configs.trustStorePath())
                        .withTrustStorePass(configs.passwords().trustStorePass())
                        .build();

                factory.setSsl(tls.getSslContext());
                factory.setPort(configs.port());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
