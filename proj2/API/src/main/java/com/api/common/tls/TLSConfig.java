package com.api.common.tls;

import javax.net.ssl.SSLException;

/**
 * Interface  TLSConfig  defines the blueprint for a TLSConfig instance
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public interface TLSConfig<T>{

    /** Constants */
    String DEFAULT_CONFIG_FILE_ERROR = "TLS Config problem. No config file was given. Check if 'clienttls.conf' exists in resources";

    /** Methods */
    void readConfigFile();
    T buildSslContext() throws SSLException;

    T getSslContext();
}
