package com.client.serviceClients;

import com.client.AbstractClient;

import javax.net.ssl.SSLException;
import java.net.URI;

public class FStorageClient extends AbstractClient {
    public FStorageClient(URI uri) throws SSLException {
        super(uri);
    }
}
