package com.api.services;

import com.api.AuthenticatePasswordRequest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface FServerAuthService {

    /**
     * Authenticates the user
     * @param username username to authenticate
     * @param password correct password
     * @return Response (boolean)
     */
    Mono<ResponseEntity<byte[]>> authenticateUser(AuthenticatePasswordRequest loginRequest);


}
