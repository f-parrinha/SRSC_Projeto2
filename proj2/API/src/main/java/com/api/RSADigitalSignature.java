package com.api;

import java.security.*;

public class RSADigitalSignature {

    private KeyPair keyPair;
    private int KEY_SIZE = 2048;

    public RSADigitalSignature() throws NoSuchAlgorithmException {
        keyPair = generateRSAKeyPair();
    }

    private KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(KEY_SIZE); // Adjust the key size based on your security requirements
        return keyPairGenerator.generateKeyPair();
    }

    public byte[] getPublicKey() {
        return keyPair.getPublic().getEncoded();
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }
}
