package com.api.common.tls;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Class  ForClient  offers methods to personalize the constructions of TLSClientConfig instances, to configure clients' TLS parameters
 */
public class TLSClientConfigBuilder {
    private final TLSClientConfig tls;

    public TLSClientConfigBuilder() {
        tls = new TLSClientConfig();
    }

    public TLSClientConfig build() throws IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, KeyManagementException {
        tls.readConfigFile();
        tls.setSslContext(tls.buildSslContext());
        tls.setSslParameters(tls.buildSslParameters());
        tls.printSSLConfigs();
        return tls;
    }

    public TLSClientConfigBuilder withConfigFile(InputStream configFile) throws FileNotFoundException {
        tls.setConfigFile(configFile);
        return this;
    }

    public TLSClientConfigBuilder withKeyStoreFile(InputStream file) {
        tls.setKeyStoreFile(file);
        return this;
    }

    public TLSClientConfigBuilder withTrustStoreFile(InputStream file) {
        tls.setTrustStoreFile(file);
        return this;
    }

    public TLSClientConfigBuilder withKeyStorePass(String pass) {
        tls.setKeyStorePass(pass);
        return this;
    }

    public TLSClientConfigBuilder withTrustStorePass(String pass) {
        tls.setTrustStorePass(pass);
        return this;
    }
}
