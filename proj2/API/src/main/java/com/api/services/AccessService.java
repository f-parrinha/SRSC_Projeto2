package com.api.services;

import java.io.IOException;

public interface AccessService<T> {
    /**
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    T rsaPublicKeyExchange() throws IOException, InterruptedException;

    T requestAccessControlToken(String token, String username) throws IOException, InterruptedException;
}
