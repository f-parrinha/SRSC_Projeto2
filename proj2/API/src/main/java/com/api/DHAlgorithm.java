package com.api;

import com.api.common.UtilsDH;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;


public class DHAlgorithm {
    private static final String ALGORITHM = "DH";
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int KEY_SIZE = 4096;
    private KeyPair dhKeyPair;
    private final KeyAgreement keyAgreement;
    private byte[] sharedSecret;

    public DHAlgorithm() throws NoSuchAlgorithmException, InvalidKeyException {
        this.keyAgreement = KeyAgreement.getInstance(ALGORITHM);
        generateDHKeys2();
    }

    public byte[] getPublicKey() {
        return dhKeyPair.getPublic().getEncoded();
    }
    public byte[] getSharedSecret(){
        return sharedSecret;
    }
    private void generateDHKeys2() throws NoSuchAlgorithmException, InvalidKeyException {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyGenerator.initialize(KEY_SIZE);
        KeyPair kPair = keyGenerator.generateKeyPair();
        keyAgreement.init(kPair.getPrivate());

        dhKeyPair = kPair;

    }

    public byte[] calculateKey(byte[] publicKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {

        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey serverPublicKey = keyFactory.generatePublic(keySpec);

        Key key = keyAgreement.doPhase(serverPublicKey, false);

        return key.getEncoded();
    }
    public void calculateSharedKey(byte[] serverPublicKeyBytes) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {

        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(serverPublicKeyBytes);
        PublicKey serverPublicKey = keyFactory.generatePublic(keySpec);

        keyAgreement.doPhase(serverPublicKey, true);

        MessageDigest hash = MessageDigest.getInstance(HASH_ALGORITHM);

        // Then A generates the final agreement key
        sharedSecret = hash.digest(keyAgreement.generateSecret());

        System.out.println("I generated: "+ UtilsDH.toHex(sharedSecret));


    }

}
