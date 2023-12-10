package com.api;

import java.security.*;
import java.util.Arrays;

public class RSADigitalSignature {

    private KeyPair keyPair;
    private int KEY_SIZE = 2048;

    public RSADigitalSignature() throws NoSuchAlgorithmException {
        keyPair = generateRSAKeyPair();
        System.out.println("RSA Key Pair generated succefully.");
        System.out.println("RSA Public Key: " + Arrays.toString(keyPair.getPublic().getEncoded()));
        System.out.println("RSA Private Key: " + keyPair.getPrivate());
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
