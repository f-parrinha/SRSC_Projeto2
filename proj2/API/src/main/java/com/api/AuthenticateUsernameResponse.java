package com.api;

public record AuthenticateUsernameResponse(byte[] secureRandom, byte[] publicKey, byte[] connectedPublicKey) {
}
