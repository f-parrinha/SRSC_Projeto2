package com.api.common.tls;

import com.api.common.shell.Shell;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;

/**
 * Class  TLSClientConfig  configures an SSL Context with custom configs for a netty client
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public class TLSClientConfig extends AbstractTLSConfig implements TLSConfig<SSLContext> {

    /** Constants */
    public static final String MANAGER_TYPE = "SunX509";

    /** Variables */
    private SSLContext sslContext;
    private SSLParameters sslParameters;
    private InputStream keyStoreFile;
    private InputStream trustStoreFile;

    @Override
    public SSLContext getSslContext() {
        return sslContext;
    }
    public SSLParameters getSslParameters() {
        return sslParameters;
    }
    @Override
    public void setSslContext(SSLContext context) { sslContext = context; }
    public void setSslParameters(SSLParameters parameters) { sslParameters = parameters; }
    public void setKeyStoreFile(InputStream file) {
        keyStoreFile = file;
    }
    public void setTrustStoreFile(InputStream file) {
        trustStoreFile = file;
    }

    @Override
    public void printSSLConfigs() {
        Shell.printDebug("SSL Configurations:");
        Shell.printDebug(" - Protocols: " + Arrays.toString(sslParameters.getProtocols()));
        Shell.printDebug(" - Ciphers: " + Arrays.toString(sslParameters.getCipherSuites()));
    }

    @Override
    public SSLContext buildSslContext() throws IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, KeyManagementException {
        KeyStore keyStore = KeyStore.getInstance(STORE_TYPE);
        KeyStore trustStore = KeyStore.getInstance(STORE_TYPE);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(MANAGER_TYPE);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(MANAGER_TYPE);

        // Load keyStores
        keyStore.load(keyStoreFile, keyStorePass.toCharArray());
        trustStore.load(trustStoreFile, trustStorePass.toCharArray());

        // Init managers
        keyManagerFactory.init(keyStore, keyStorePass.toCharArray());
        trustManagerFactory.init(trustStore);

        // Define ssl context
        SSLContext ssl = SSLContext.getInstance(TLS_PROTOCOL);
        ssl.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        return ssl;
    }

    public SSLParameters buildSslParameters() {
        SSLParameters params = new SSLParameters();
        params.setCipherSuites(ciphers);
        params.setProtocols(protocols);
        return params;
    }
}
