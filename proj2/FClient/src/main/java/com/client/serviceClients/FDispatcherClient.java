package com.client.serviceClients;

import com.api.requests.RestRequest;
import com.api.requests.CopyRequest;
import com.api.requests.MkDirRequest;
import com.client.AbstractClient;
import com.api.requests.LoginRequest;
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
    public FDispatcherClient(URI uri, SSLContext sslContext, SSLParameters sslParameters) throws NoSuchAlgorithmException, KeyManagementException {
        super(uri, sslContext, sslParameters);
    }


    @Override
    public HttpResponse<String> login(LoginRequest loginRequest) throws IOException, InterruptedException {
        JsonObject loginJson = loginRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).post("/login", loginJson);
        return client.send(request,  HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> listFiles(String username) throws IOException, InterruptedException {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/ls/{username}", username);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> listFiles(String username, String path) throws IOException, InterruptedException {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/ls/{username}/{path}", username, path);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> makeDirectory(String username, MkDirRequest mkDirRequest) throws IOException, InterruptedException {
        JsonObject mkDirJson = mkDirRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).post("/mkdir/{username}", mkDirJson, username);
        return client.send(request,  HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> put(String username, String path, String fileName) {
        /*@TODO:
         *  - Add File resource
         *  - Add request using that resource and place it on body
         */
        return null;
    }

    @Override
    public HttpResponse<String> get(String username, String path, String fileName) throws IOException, InterruptedException {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/get/{username}/{path}/{fileName}", username, path, fileName);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> copy(String username, CopyRequest copyRequest) throws IOException, InterruptedException {
        JsonObject copyJson = copyRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).post("/cp/{username}", copyJson, username);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> remove(String username, String path, String fileName) throws IOException, InterruptedException {
        HttpRequest request = RestRequest.getInstance(baseUri).delete("/rm/{username}/{path}/{file}", username, path, fileName);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> file(String username, String path, String fileName) throws IOException, InterruptedException {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/file/{username}/{path}/{file}", username, path, fileName);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
