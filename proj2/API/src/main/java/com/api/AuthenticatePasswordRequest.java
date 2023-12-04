package com.api;

public record AuthenticatePasswordRequest(byte[] cipheredData, byte[] secureRandom, byte[] publicKey, byte[] connectedPublicKey) {
}
