package com.client.serviceClients;

import com.api.rest.requests.authenticate.AuthenticatePasswordRequest;
import com.api.rest.RestRequest;
import com.api.services.AuthService;
import com.api.utils.JwtTokenUtil;
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
    public HttpResponse<String> rsaPublicKeyExchange()  {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/request-RSA-Key", JwtTokenUtil.NO_TOKEN, JwtTokenUtil.NO_TOKEN);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> requestDHPublicKey(String username) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/auth/init-connection/{username}", JwtTokenUtil.NO_TOKEN, JwtTokenUtil.NO_TOKEN, username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> authenticateUser(String loginRequest, String username) {
        AuthenticatePasswordRequest req = AuthenticatePasswordRequest.fromJsonString(loginRequest);
        JsonObject requestKeyExchangeJson = req.serialize();

        HttpRequest request = RestRequest.getInstance(baseUri).post("/auth/login/{username}", JwtTokenUtil.NO_TOKEN, JwtTokenUtil.NO_TOKEN, requestKeyExchangeJson, username);
        return sendRequest(request);
        }
}
