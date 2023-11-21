package com.client.serviceClients;

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
    public Mono<ResponseEntity<Boolean>> authenticateUser(String username, String password) {
        return webClient.post()
                .uri("/authenticate")
                .body(Mono.just(new LoginRequest(username, password)), LoginRequest.class)
                .retrieve()
                .toEntity(Boolean.class);
    }
}
