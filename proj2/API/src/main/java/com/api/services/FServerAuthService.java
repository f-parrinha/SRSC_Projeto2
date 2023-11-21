package com.api.services;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface FServerAuthService {

    /**
     * Authenticates the user
     * @param username username to authenticate
     * @param password correct password
     * @return Response (boolean)
     */
    Mono<ResponseEntity<Boolean>> authenticateUser(String username, String password);
}
