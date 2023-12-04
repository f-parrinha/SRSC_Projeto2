package com.client.serviceClients;

import com.api.AuthenticatePasswordRequest;
import com.api.AuthenticateUsernameRequest;
import com.api.AuthenticateUsernameResponse;
import com.api.LoginRequest;
import com.api.services.FServerAuthService;
import com.client.AbstractClient;
import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
public class FAuthClient extends AbstractClient {
    public FAuthClient(URI uri, SSLContext sslContext, SSLParameters sslParameters) throws NoSuchAlgorithmException, KeyManagementException {
        super(uri, sslContext, sslParameters);
    }
    @Override
    public Mono<ResponseEntity<byte[]>> authenticateUser(AuthenticatePasswordRequest loginRequest) {
        return webClient.post()
                .uri("/authenticate")
                .body(Mono.just(loginRequest), AuthenticatePasswordRequest.class)
                .retrieve()
                .toEntity(byte[].class);
    }
    public Mono<ResponseEntity<AuthenticateUsernameResponse>> requestDHPublicKey(AuthenticateUsernameRequest request) {
        return webClient.post()
                .uri("/init-connection-auth")
                .body(Mono.just(request), AuthenticateUsernameRequest.class)
                .retrieve()
                .toEntity(AuthenticateUsernameResponse.class);


}
