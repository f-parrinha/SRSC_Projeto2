package com.api.common.tls;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Class  ForServer  offers methods to personalize the constructions of TLSServerConfig instances, to configure servers' TLS parameters
 */
public class TLSServerConfigBuilder {
    private final TLSServerConfig tls;

    public TLSServerConfigBuilder() {
        tls = new TLSServerConfig();
    }

    public TLSServerConfig build() {
        tls.readConfigFile();
        tls.setSslContext(tls.buildSslContext());
        tls.printSSLConfigs();
        return tls;
    }

    public TLSServerConfigBuilder withConfigFile(InputStream configFile) throws FileNotFoundException {
        tls.setConfigFile(configFile);
        return this;
    }

    public TLSServerConfigBuilder withKeyStorePath(String path) {
        tls.setKeyStorePath(path);
        return this;
    }

    public TLSServerConfigBuilder withKeyStorePass(String pass) {
        tls.setKeyStorePass(pass);
        return this;
    }

    public TLSServerConfigBuilder withKeyAlias(String alias) {
        tls.setKeyAlias(alias);
        return this;
    }

    public TLSServerConfigBuilder withKeyPass(String pass) {
        tls.setKeyPass(pass);
        return this;
    }

    public TLSServerConfigBuilder withTrustStorePath(String path) {
        tls.setTrustStorePath(path);
        return this;
    }

    public TLSServerConfigBuilder withTrustStorePass(String pass) {
        tls.setTrustStorePass(pass);
        return this;
    }
}
