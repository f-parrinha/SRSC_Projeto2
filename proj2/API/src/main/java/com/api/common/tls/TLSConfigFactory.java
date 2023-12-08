package com.api.common.tls;

import com.api.common.shell.Shell;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class TLSConfigFactory {
    private static TLSConfigFactory instance;

    private TLSConfigFactory() {
        // Nothing to do here...
    }

    public static TLSConfigFactory getInstance() {
        instance = instance == null ? new TLSConfigFactory() : instance;
        return instance;
    }
    
    public ClientBuilder forClient() { return new ClientBuilder(); }
    public ServerBuilder forServer() { return new ServerBuilder(); }


    /**
     * Class  ForClient  offers methods to personalize the constructions of TLSClientConfig instances, to configure clients' TLS parameters
     */
    public static final class ClientBuilder {
        private final TLSClientConfig tls;
    
        public ClientBuilder() {
            tls = new TLSClientConfig();
        }
    
        public TLSClientConfig build() {
            try {
                tls.readConfigFile();
                tls.setSslContext(tls.buildSslContext());
                tls.setSslParameters(tls.buildSslParameters());
                tls.printSSLConfigs();
            } catch (IOException e) {
                Shell.printError("Error while reading configuration file.");
            } catch (KeyStoreException e) {
                Shell.printError("There was a problem while initializing the keystore.");
            } catch (NoSuchAlgorithmException e) {
                Shell.printError("There is no such algorithm for TLS support.");
            } catch (UnrecoverableKeyException e) {
                Shell.printError("Unrecoverable key.");
            } catch (CertificateException e) {
                Shell.printError("No certificate found.");
            } catch (KeyManagementException e) {
                Shell.printError("There was a problem related to the key management system.");
            }

            return tls;
        }
    
        public ClientBuilder withConfigFile(InputStream configFile) {
            try {
                tls.setConfigFile(configFile);
            } catch (Exception e) {
                Shell.printError("Config file not found");
            }
            return this;
        }
    
        public ClientBuilder withKeyStoreFile(InputStream file) {
            tls.setKeyStoreFile(file);
            return this;
        }
    
        public ClientBuilder withTrustStoreFile(InputStream file) {
            tls.setTrustStoreFile(file);
            return this;
        }
    
        public ClientBuilder withKeyStorePass(String pass) {
            tls.setKeyStorePass(pass);
            return this;
        }
    
        public ClientBuilder withTrustStorePass(String pass) {
            tls.setTrustStorePass(pass);
            return this;
        }
    }

    /**
     * Class  ForServer  offers methods to personalize the constructions of TLSServerConfig instances, to configure servers' TLS parameters
     */
    public static final class ServerBuilder {
        private final TLSServerConfig tls;
    
        public ServerBuilder() {
            tls = new TLSServerConfig();
        }
    
        public TLSServerConfig build() {
            tls.readConfigFile();
            tls.setSslContext(tls.buildSslContext());
            tls.printSSLConfigs();
            return tls;
        }
    
        public ServerBuilder withConfigFile(InputStream configFile) throws FileNotFoundException {
            tls.setConfigFile(configFile);
            return this;
        }
    
        public ServerBuilder withKeyStorePath(String path) {
            tls.setKeyStorePath(path);
            return this;
        }
    
        public ServerBuilder withKeyStorePass(String pass) {
            tls.setKeyStorePass(pass);
            return this;
        }
    
        public ServerBuilder withKeyAlias(String alias) {
            tls.setKeyAlias(alias);
            return this;
        }
    
        public ServerBuilder withKeyPass(String pass) {
            tls.setKeyPass(pass);
            return this;
        }
    
        public ServerBuilder withTrustStorePath(String path) {
            tls.setTrustStorePath(path);
            return this;
        }
    
        public ServerBuilder withTrustStorePass(String pass) {
            tls.setTrustStorePass(pass);
            return this;
        }
    }
}
