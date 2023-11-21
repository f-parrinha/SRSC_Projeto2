package com.client.serviceClients;

import com.client.AbstractClient;

import javax.net.ssl.SSLException;
import java.net.URI;

public class FAuthClient extends AbstractClient {
    public FAuthClient(URI uri) throws SSLException {
        super(uri);
    }
}
