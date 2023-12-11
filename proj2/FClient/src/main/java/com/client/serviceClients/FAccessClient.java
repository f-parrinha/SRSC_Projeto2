package com.client.serviceClients;

import com.api.rest.RestRequest;
import com.api.services.AccessService;
import com.api.utils.JwtTokenUtil;
import com.client.AbstractClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class FAccessClient extends AbstractClient implements AccessService<HttpResponse<String>> {
    public FAccessClient(URI uri, SSLContext sslContext, SSLParameters sslParameters) {
        super(uri, sslContext, sslParameters);
    }

    @Override
    public HttpResponse<String> rsaPublicKeyExchange() throws IOException, InterruptedException {
        HttpRequest httpRequest = RestRequest.getInstance(baseUri).get("/access/RSAKeyExchange", JwtTokenUtil.NO_TOKEN, JwtTokenUtil.NO_TOKEN);
        return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> requestAccessControlToken(String token, String username) throws IOException, InterruptedException {

        HttpRequest httpRequest = RestRequest.getInstance(baseUri).get("/access/token/{username}", token, username);
        return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }
}
