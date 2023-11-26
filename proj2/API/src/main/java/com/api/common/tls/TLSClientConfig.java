package com.api.common.tls;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;

/**
 * Class  TLSClientConfig  configures an SSL Context with custom configs for a netty client
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public class TLSClientConfig extends AbstractTLSConfig implements TLSConfig<SslContext> {

    /** Constants */
    public static final String STORE_TYPE = "JKS";

    /** Variables */
    private SslContext sslContext;
    private Iterable<String> protocols;
    private Iterable<String> ciphers;
    private TrustManagerFactory trustManagerFactory;
    private KeyManagerFactory keyManagerFactory;
    private InputStream keyStoreFile;
    private InputStream trustStoreFile;


    @Override
    public void readConfigFile() {
        if (configFile == null) {
            System.out.println(DEFAULT_CONFIG_FILE_ERROR);
            return;
        }

        // Try to read file
        try (InputStreamReader reader = new InputStreamReader(configFile)){
            BufferedReader bufferedReader = new BufferedReader(reader);

            // Assign read values
            this.protocols = List.of(bufferedReader.readLine().split(":")[1].trim());
            this.auth = bufferedReader.readLine().split(":")[1];
            this.ciphers = List.of(bufferedReader.readLine().split(":")[1].trim());
        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
    }

    @Override
    public SslContext buildSslContext() throws SSLException {
        return SslContextBuilder.forClient()
                .protocols(protocols)
                .ciphers(ciphers)
                .keyManager(keyManagerFactory)
                .trustManager(trustManagerFactory)
                .build();
    }

    @Override
    public SslContext getSslContext() {
        return sslContext;
    }
    public void setKeyStoreFile(InputStream file) {
        keyStoreFile = file;
    }
    public void setTrustStoreFile(InputStream file) {
        trustStoreFile = file;
    }

    /**
     * Initiates trustStores, keyStores and their managers
     * @throws NoSuchAlgorithmException wrong store algorithm
     * @throws UnrecoverableKeyException bug with key
     * @throws KeyStoreException exception during keyStore usage
     * @throws CertificateException wrong certificate
     * @throws IOException input output problems
     */
    public void initTrustMaterial() throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, CertificateException, IOException {
        // Setup keyStores and trustStores
        KeyStore keyStore = KeyStore.getInstance(STORE_TYPE);
        KeyStore trustStore = KeyStore.getInstance(STORE_TYPE);
        keyStore.load(keyStoreFile, keyStorePass.toCharArray());
        trustStore.load(trustStoreFile, trustStorePass.toCharArray());

        // Init trust and key manager factories
        keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyStorePass.toCharArray());
        trustManagerFactory.init(trustStore);
    }

    public static class Builder {
        private final TLSClientConfig tls;
        public Builder(){
            tls = new TLSClientConfig();
        }

        public TLSClientConfig build() throws IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException {
            tls.readConfigFile();
            tls.initTrustMaterial();
            this.tls.sslContext = tls.buildSslContext();
            return tls;
        }
        public Builder withConfigFile(InputStream configFile) throws FileNotFoundException {
            this.tls.setConfigFile(configFile);
            return this;
        }
        public Builder withKeyStoreFile(InputStream file) {
            this.tls.setKeyStoreFile(file);
            return this;
        }
        public Builder withTrustStoreFile(InputStream file) {
            this.tls.setTrustStoreFile(file);
            return this;
        }
        public Builder withKeyStorePass(String pass) {
            this.tls.setKeyStorePass(pass);
            return this;
        }
        public Builder withTrustStorePass(String pass) {
            this.tls.setTrustStorePass(pass);
            return this;
        }
    }
}
