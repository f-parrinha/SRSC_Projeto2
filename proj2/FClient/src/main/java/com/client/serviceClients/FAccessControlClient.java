package com.client.serviceClients;

import com.api.services.FServerAccessControlService;
import com.client.AbstractClient;

public class FAccessControlClient extends AbstractClient implements FServerAccessControlService {
    public FAccessControlClient(String URL) {
        super(URL);
    }
}
