package com.api.common.tls;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Abstract Class  AbstractTLSConfig  define custom parameters for TLS configuration classes
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public abstract class AbstractTLSConfig {

    /** Variables */
    protected InputStream configFile;
    protected String keyStorePath;
    protected String keyStorePass;
    protected String trustStorePath;
    protected String trustStorePass;
    protected String auth;


    /** Methods */
    public void setConfigFile(InputStream configFile) throws FileNotFoundException {
        this.configFile = configFile;
    }
    public void setKeyStorePath(String path) {
        keyStorePath = path;
    }
    public void setKeyStorePass(String pass) {
        keyStorePass = pass;
    }
    public void setTrustStorePath(String path) {
        trustStorePath = path;
    }
    public void setTrustStorePass(String pass) {
        trustStorePass = pass;
    }
}
