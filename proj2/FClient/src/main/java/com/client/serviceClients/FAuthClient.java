package com.client.serviceClients;

import com.api.AuthenticatePasswordRequest;
import com.api.AuthenticateUsernameRequest;
import com.api.AuthenticateUsernameResponse;
import com.api.LoginRequest;
import com.api.services.FServerAuthService;
import com.client.AbstractClient;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public class FAuthClient extends AbstractClient implements FServerAuthService {
    public FAuthClient(String URL) {
        super(URL);
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

}
