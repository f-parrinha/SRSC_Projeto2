package com.client.serviceClients;

import com.client.AbstractClient;
import io.netty.handler.ssl.SslContext;

import java.io.IOException;
import java.net.URI;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class FAuthClient extends AbstractClient {
    public FAuthClient(URI uri, SslContext sslContext) {
        super(uri, sslContext);
    }
}
