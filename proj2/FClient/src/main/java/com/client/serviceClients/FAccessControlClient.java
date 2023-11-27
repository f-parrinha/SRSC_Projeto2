package com.client.serviceClients;

import com.client.AbstractClient;
import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class FAccessControlClient extends AbstractClient {
    public FAccessControlClient(URI uri, SSLContext sslContext, SSLParameters sslParameters) throws NoSuchAlgorithmException, KeyManagementException {
        super(uri, sslContext, sslParameters);
    }
}
