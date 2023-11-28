package com.client.serviceClients;

import com.api.requests.MkDirRequest;
import com.api.requests.RestRequest;
import com.api.services.StorageService;
import com.client.AbstractClient;
import io.netty.handler.ssl.SslContext;

import javax.json.JsonObject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class FStorageClient extends AbstractClient implements StorageService<HttpResponse<String>> {
    public FStorageClient(URI uri, SSLContext sslContext, SSLParameters sslParameters) throws NoSuchAlgorithmException, KeyManagementException {
        super(uri, sslContext, sslParameters);
    }

    @Override
    public HttpResponse<String> createDirectory(String username, MkDirRequest mkDirRequest) throws IOException, InterruptedException {
        JsonObject mkDirJson = mkDirRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).post("/storage/mkdir/{username}", mkDirJson, username);
        return client.send(request,  HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> getFile() {
        return null;
    }

    @Override
    public HttpResponse<String> createFile() {
        return null;
    }

    @Override
    public HttpResponse<String> removeFile() {
        return null;
    }

    @Override
    public HttpResponse<String> listContent() {
        return null;
    }

    @Override
    public HttpResponse<String> copyFile() {
        return null;
    }
}
