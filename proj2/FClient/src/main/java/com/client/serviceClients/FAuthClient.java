package com.client.serviceClients;

import com.api.requests.authenticate.AuthenticatePasswordRequest;
import com.api.requests.RestRequest;
import com.api.services.AuthService;
import com.client.AbstractClient;

import javax.json.JsonObject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FAuthClient extends AbstractClient implements AuthService<HttpResponse<String>> {
    public FAuthClient(URI uri, SSLContext sslContext, SSLParameters sslParameters) {
        super(uri, sslContext, sslParameters);
    }

    @Override
    public HttpResponse<String> rsaPublicKeyExchange() throws IOException, InterruptedException {
        HttpRequest httpRequest = RestRequest.getInstance(baseUri).get("/request-RSA-Key");
        return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }
    @Override
    public HttpResponse<String> requestDHPublicKey(String username) throws IOException, InterruptedException {
        HttpRequest httpRequest = RestRequest.getInstance(baseUri).get("/auth/init-connection/{username}", "", username);
        return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> authenticateUser(String loginRequest, String username) throws IOException, InterruptedException {
        AuthenticatePasswordRequest req = AuthenticatePasswordRequest.fromJsonString(loginRequest);
        JsonObject requestKeyExchangeJson = req.serialize();

        HttpRequest httpRequest = RestRequest.getInstance(baseUri).post("/auth/login/{username}", requestKeyExchangeJson, username);
        return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }


}
