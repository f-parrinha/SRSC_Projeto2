package com.api.common.tls;

import com.api.common.shell.Shell;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract Class  AbstractTLSConfig  defines custom parameters for TLS configuration classes
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public abstract class AbstractTLSConfig {

    /** Constants */
    public static String TLS_PROTOCOL = "TLS";
    public static final String STORE_TYPE = "JKS";
    public static String DEFAULT_CONFIG_FILE_ERROR = "TLS Config problem. No config file was given. Check if 'clienttls.conf' exists in resources";

    /** Variables */
    protected InputStream configFile;
    protected String[] protocols;
    protected String[] ciphers;
    protected String keyStorePath;
    protected String keyStorePass;
    protected String trustStorePath;
    protected String trustStorePass;
    protected AuthType auth;

    public enum AuthType {
        SERV,
        MUTUAL
    }

    public void readConfigFile() {
        if (configFile == null) {
            Shell.printError(DEFAULT_CONFIG_FILE_ERROR);
            return;
        }

        // Try to read file
        try (InputStreamReader reader = new InputStreamReader(configFile)){
            BufferedReader bufferedReader = new BufferedReader(reader);

            // Assign read values
            this.protocols = bufferedReader.readLine().split(":")[1].trim().split("\\s*,\\s*");
            this.auth = AuthType.valueOf(bufferedReader.readLine().split(":")[1].trim());
            this.ciphers = bufferedReader.readLine().split(":")[1].trim().split("\\s*,\\s*");
        } catch (IOException e) {
            Shell.printError(Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
    }
    public abstract void printSSLConfigs();
    public void setConfigFile(InputStream configFile) {
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

    private String[] listToArray(List<String> list) {
        String[] result = new String[list.size()];
        int idx = 0;

        for(String el : list) {
            result[idx++] = el;
        }

        return result;
    }
}
