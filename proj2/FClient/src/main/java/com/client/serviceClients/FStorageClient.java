package com.client.serviceClients;

import com.api.rest.requests.CopyRequest;
import com.api.rest.requests.MkDirRequest;
import com.api.rest.RestRequest;
import com.api.rest.requests.PutRequest;
import com.api.services.StorageService;
import com.api.utils.JwtTokenUtil;
import com.client.AbstractClient;

import javax.json.JsonObject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FStorageClient extends AbstractClient implements StorageService<HttpResponse<String>> {
    public FStorageClient(URI uri, SSLContext sslContext, SSLParameters sslParameters) {
        super(uri, sslContext, sslParameters);
    }

    @Override
    public HttpResponse<String> listDirectories(String username) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/storage/ls/{username}", JwtTokenUtil.NO_TOKEN, JwtTokenUtil.NO_TOKEN, username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> listDirectories(String username, String path) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/storage/ls/{username}/{path}", JwtTokenUtil.NO_TOKEN, JwtTokenUtil.NO_TOKEN, username, path);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> createFolder(String username, MkDirRequest mkDirRequest) {
        JsonObject json = mkDirRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).post("/storage/mkdir/{username}", JwtTokenUtil.NO_TOKEN, JwtTokenUtil.NO_TOKEN, json, username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> getFile(String username, String path) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/storage/get/{username}/{path}", JwtTokenUtil.NO_TOKEN, JwtTokenUtil.NO_TOKEN, username, path);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> createFile(String username, PutRequest putRequest) {
        JsonObject json = putRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).put("/storage/put/{username}", JwtTokenUtil.NO_TOKEN, JwtTokenUtil.NO_TOKEN, json, username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> removeFile(String username, String path) {
        HttpRequest request = RestRequest.getInstance(baseUri).delete("/storage/rm/{username}/{path}", JwtTokenUtil.NO_TOKEN, JwtTokenUtil.NO_TOKEN, username, path);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> copyFile(String username, CopyRequest cpRequest) {
        JsonObject json = cpRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).put("/storage/cp/{username}", JwtTokenUtil.NO_TOKEN, JwtTokenUtil.NO_TOKEN, json, username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> fileProperties(String username, String path) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/storage/file/{username}/{path}", JwtTokenUtil.NO_TOKEN, JwtTokenUtil.NO_TOKEN, username, path);
        return sendRequest(request);
    }
}
