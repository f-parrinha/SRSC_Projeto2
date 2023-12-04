package com.client.serviceClients;

import com.api.services.FServerStorageService;
import com.client.AbstractClient;


public class FStorageClient extends AbstractClient implements FServerStorageService {
    public FStorageClient(String URL) {
        super(URL);
    }
}
