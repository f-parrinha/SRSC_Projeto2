package com.api.services;

import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

public interface AuthService<T> {
    /**
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    T rsaPublicKeyExchange() throws IOException, InterruptedException;
    /**
     *
     * @param request
     * @return
     */
    T requestDHPublicKey(String request) throws IOException, InterruptedException;

    /**
     *
     * @param stringLoginRequest
     * @return
     * @throws Exception
     */
    T authenticateUser(String stringLoginRequest, String username) throws Exception;


}
