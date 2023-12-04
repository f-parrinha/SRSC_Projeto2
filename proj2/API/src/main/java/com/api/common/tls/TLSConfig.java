package com.api.common.tls;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Interface  TLSConfig  defines the blueprint for a TLSConfig instance
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public interface TLSConfig<T>{

    /**
     * Creates a new SSL Context for the Tomcat servlet
     * @return SpringBoot Ssl
     */
    T buildSslContext() throws IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, KeyManagementException;

    /**
     * Gets the configuration's ssl context
     * @return ssl context object
     */
    T getSslContext();

    /**
     * Sets the SSL context to a new one
     * @param context new SSL context
     */
    void setSslContext(T context);
}