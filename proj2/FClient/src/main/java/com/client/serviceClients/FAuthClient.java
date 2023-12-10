package com.client.serviceClients;

import com.api.auth.AuthenticationToken;
import com.api.rest.requests.authenticate.AuthenticatePasswordRequest;
import com.api.rest.RestRequest;
import com.api.rest.requests.authenticate.RequestKeyExchange;
import com.api.rest.requests.authenticate.AuthenticatePasswordRequest;
import com.api.rest.RestRequest;
import com.api.services.AuthService;
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

public class FAuthClient extends AbstractClient implements AuthService<HttpResponse<String>> {
    public FAuthClient(URI uri, SSLContext sslContext, SSLParameters sslParameters) {
        super(uri, sslContext, sslParameters);
    }

    @Override
    public HttpResponse<String> rsaPublicKeyExchange()  {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/request-RSA-Key", AuthenticationToken.EMPTY);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> requestDHPublicKey(String username) throws IOException, InterruptedException {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/auth/init-connection/{username}", "", username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> authenticateUser(String loginRequest, String username) {
        AuthenticatePasswordRequest req = AuthenticatePasswordRequest.fromJsonString(loginRequest);
        JsonObject requestKeyExchangeJson = req.serialize();

        HttpRequest request = RestRequest.getInstance(baseUri).post("/auth/login/{username}", requestKeyExchangeJson, username);
        return sendRequest(request);
        }
}
