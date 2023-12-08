package com.client.serviceClients;

import com.api.rest.requests.CopyRequest;
import com.api.rest.requests.LoginRequest;
import com.api.rest.requests.MkDirRequest;
import com.api.rest.RestRequest;
import com.api.rest.requests.PutRequest;
import com.client.AbstractClient;
import com.api.services.DispatcherService;

import javax.json.JsonObject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Class  FDispatcherClient  offers tools to create requests to the FServer (Dispatcher module)
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */

public class FDispatcherClient extends AbstractClient implements DispatcherService<HttpResponse<String>> {
    public FDispatcherClient(URI uri, SSLContext sslContext, SSLParameters sslParameters) {
        super(uri, sslContext, sslParameters);
    }


    @Override
    public HttpResponse<String> login(LoginRequest loginRequest) {
        JsonObject json = loginRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).post("/login", json);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> listFiles(String username) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/ls/{username}", username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> listFiles(String username, String path) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/ls/{username}/{path}", username, path);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> makeDirectory(String username, MkDirRequest mkDirRequest) {
        JsonObject json = mkDirRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).post("/mkdir/{username}", json, username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> put(String username, PutRequest putRequest) {
        JsonObject json = putRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).put("/put/{username}", json, username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> get(String username, String path) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/get/{username}/{path}", username, path);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> copy(String username, CopyRequest copyRequest) {
        JsonObject json = copyRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).post("/cp/{username}", json, username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> remove(String username, String path) {
        HttpRequest request = RestRequest.getInstance(baseUri).delete("/rm/{username}/{path}", username, path);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> file(String username, String path) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/file/{username}/{path}", username, path);
        return sendRequest(request);
    }
}
