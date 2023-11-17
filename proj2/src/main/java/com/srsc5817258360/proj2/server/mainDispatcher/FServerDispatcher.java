package com.srsc5817258360.proj2.server.mainDispatcher;

import com.srsc5817258360.proj2.client.FAccessControlClient;
import com.srsc5817258360.proj2.client.FAuthClient;
import com.srsc5817258360.proj2.client.FStorageClient;
import com.srsc5817258360.proj2.server.FServer;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FServerDispatcher extends FServer {

    private final FAuthClient authClient;
    private final FAccessControlClient accessControlClient;
    private final FStorageClient fStorageClient;
    public FServerDispatcher(FAuthClient authClient, FAccessControlClient accessControlClient, FStorageClient fStorageClient) {
        this.authClient = authClient;
        this.accessControlClient = accessControlClient;
        this.fStorageClient = fStorageClient;
    }
}
