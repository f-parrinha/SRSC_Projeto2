package com.api.common.tls;

import org.springframework.boot.web.server.Ssl;

import java.io.*;
import java.util.Arrays;


/**
 * Class  TLSServerConfig  configures an SSL Context with custom configs for a Tomcat servlet, used by Spring
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public class TLSServerConfig extends AbstractTLSConfig implements TLSConfig<Ssl> {

    /** Constants */
    public static final String STORE_TYPE = "JKS";

    /** Variables */
    private String keyAlias;
    private String keyPass;
    private Ssl sslContext;
    private String protocol;
    private String[] ciphers;


    /**
     * Reads the configuration file to assign config values
     */
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
            this.protocol = bufferedReader.readLine().split(":")[1].trim();
            this.auth = bufferedReader.readLine().split(":")[1];
            this.ciphers = bufferedReader.readLine().split(":")[1].trim().split("\\s,\\s");
        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
    }

    @Override
    public Ssl getSslContext() {
        return sslContext;
    }
    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }
    public void setKeyPass(String keyPass) {
        this.keyPass = keyPass;
    }

    /**
     * Creates a new SSL Context for the Tomcat servlet
     * @return SpringBoot Ssl
     */
    @Override
    public Ssl buildSslContext() {
        Ssl ssl = new Ssl();
        ssl.setProtocol(protocol);
        ssl.setCiphers(ciphers);
        ssl.setKeyStoreType(STORE_TYPE);
        ssl.setKeyStore(keyStorePath);
        ssl.setKeyStorePassword(keyStorePass);
        ssl.setKeyAlias(keyAlias);
        ssl.setKeyPassword(keyPass);
        ssl.setTrustStoreType(STORE_TYPE);
        ssl.setTrustStore(trustStorePath);
        ssl.setTrustStorePassword(trustStorePass);
        ssl.setEnabled(true);

        return ssl;
    }


    public static class Builder {
        private final TLSServerConfig tls;
        public Builder(){
            tls = new TLSServerConfig();
        }

        public TLSServerConfig build() {
            tls.readConfigFile();
            this.tls.sslContext = tls.buildSslContext();
            return tls;
        }
        public Builder withConfigFile(InputStream configFile) throws FileNotFoundException {
            this.tls.setConfigFile(configFile);
            return this;
        }
        public Builder withKeyStorePath(String path) {
            this.tls.setKeyStorePath(path);
            return this;
        }
        public Builder withKeyStorePass(String pass) {
            this.tls.setKeyStorePass(pass);
            return this;
        }
        public Builder withKeyAlias(String alias) {
            this.tls.setKeyAlias(alias);
            return this;
        }
        public Builder withKeyPass(String pass) {
            this.tls.setKeyPass(pass);
            return this;
        }
        public Builder withTrustStorePath(String path) {
            this.tls.setTrustStorePath(path);
            return this;
        }
        public Builder withTrustStorePass(String pass) {
            this.tls.setTrustStorePass(pass);
            return this;
        }
    }
}
