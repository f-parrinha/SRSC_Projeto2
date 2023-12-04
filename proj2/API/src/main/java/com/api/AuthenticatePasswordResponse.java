package com.api;

import java.io.Serializable;

public record AuthenticatePasswordResponse(byte[] signedData, byte[] plainData, byte[] secureRandom, byte[] rsaPublicKey) implements Serializable {
}
