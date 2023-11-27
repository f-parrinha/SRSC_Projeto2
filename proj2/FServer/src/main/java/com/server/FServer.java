package com.server;

import com.api.common.shell.Shell;
import com.api.common.shell.StorePasswords;
import com.api.common.tls.TLSServerConfig;
import com.api.common.tls.TLSServerConfigBuilder;
import com.server.modules.FDispatcher;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

public abstract class FServer {

    public static void main(String[] args) {
        SpringApplication.run(FDispatcher.class, args);
    }

    public abstract WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> serverConfig();

    protected WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> createWebServerFactory(ServerConfigs configs) {
        return factory -> {
            try {
                TLSServerConfig tls = new TLSServerConfigBuilder()
                        .withConfigFile(configs.configFile())
                        .withKeyStorePath(configs.keyStorePath())
                        .withKeyStorePass(configs.passwords().keyStorePass())
                        .withKeyAlias(configs.keyAlias())
                        .withKeyPass(configs.passwords().keyStorePass())
                        .withTrustStorePath(configs.trustStorePath())
                        .withTrustStorePass(configs.passwords().trustStorePass())
                        .build();

                Shell.printDebug(tls.getSslContext().getProtocol() + " - " + Arrays.toString(tls.getSslContext().getEnabledProtocols()));
                factory.setSsl(tls.getSslContext());
                factory.setPort(configs.port());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
