package com.client.serviceClients;

import com.client.AbstractClient;

import javax.net.ssl.SSLException;
import java.net.URI;

public class FAccessControlClient extends AbstractClient {
    public FAccessControlClient(URI uri) throws SSLException {
        super(uri);
    }
}
