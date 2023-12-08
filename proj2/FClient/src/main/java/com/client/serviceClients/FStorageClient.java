package com.client.serviceClients;

import com.api.rest.requests.MkDirRequest;
import com.api.rest.RestRequest;
import com.api.rest.requests.PutRequest;
import com.api.services.StorageService;
import com.client.AbstractClient;
import org.springframework.web.bind.annotation.PutMapping;

import javax.json.JsonObject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class FStorageClient extends AbstractClient implements StorageService<HttpResponse<String>> {
    public FStorageClient(URI uri, SSLContext sslContext, SSLParameters sslParameters) {
        super(uri, sslContext, sslParameters);
    }

    @Override
    public HttpResponse<String> listDirectories(String username) {
        HttpRequest request = RestRequest.getInstance(baseUri, true).get("/storage/ls/{username}", username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> listDirectories(String username, String path) {
        HttpRequest request = RestRequest.getInstance(baseUri, true).get("/storage/ls/{username}/{path}", username, path);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> createFolder(String username, MkDirRequest mkDirRequest) {
        JsonObject json = mkDirRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri, true).post("/storage/mkdir/{username}", json, username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> getFile(String username, String path) {
        HttpRequest request = RestRequest.getInstance(baseUri, true).get("/storage/get/{username}/{path}", username, path);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> createFile(String username, PutRequest putRequest) {
        JsonObject json = putRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri, true).put("/storage/put/{username}", json, username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> removeFile(String username, String path) {
        HttpRequest request = RestRequest.getInstance(baseUri, true).delete("/storage/rm/{username}/{path}", username, path);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> copyFile() {
        return null;
    }
}
