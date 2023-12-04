package com.api;

public record AuthenticateUsernameRequest(byte[] username, byte[] publicKey) {
}
