package com.api;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static com.api.common.UtilsBase.bytesToLong;
import static com.api.common.UtilsBase.concatArrays;

public class SecureLogin {
    private static final short NONCE_SIZE = 16;
    private final DHAlgorithm dhAlgorithm;
    private final CipherAESAlgorithm cipherAESAlgorithm;
    private RSADigitalSignature rsaDigitalSignature;
    private final SecureRandom secureRandomGenerator;
    private byte[] secureRandom;

    public SecureLogin() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
        dhAlgorithm = new DHAlgorithm();
        cipherAESAlgorithm = new CipherAESAlgorithm();
        secureRandomGenerator = new SecureRandom();
    }

    /**
     * @return
     */
    public byte[] generateRandomBytes() {

        byte[] random = new byte[NONCE_SIZE];
        secureRandomGenerator.nextBytes(random);
        secureRandom = random;

        return random;
    }

    /**
     * @return
     */
    public byte[] getSecureRandom() {
        return secureRandom;
    }

    /**
     * @return
     */
    public byte[] getDHPublicKey() {
        return dhAlgorithm.getPublicKey();
    }

    public byte[] calculateKey(byte[] publicKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        return dhAlgorithm.calculateKey(publicKeyBytes);
    }

    /**
     * @param publicDHKey
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     */
    public void generateDHSharedSecret(byte[] publicDHKey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        dhAlgorithm.calculateSharedKey(publicDHKey);
    }

    /**
     * @param pwd
     * @param nonce
     * @return
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public AuthenticatePasswordRequest formLoginRequest(String pwd, byte[] nonce, byte[] connectedPublicKey) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        //{ H (PWD) || SecureRandom1+1 } Ks || SecureRandom2 || Assinatura de Ycliente || Assinatura A-C

        byte[] hashedPWD = cipherAESAlgorithm.hashData(pwd.getBytes());
        byte[] data = concatArrays(hashedPWD, nonce);

        byte[] encryptedPayload = cipherAESAlgorithm.encryptHashedData(data, dhAlgorithm.getSharedSecret(), secureRandom);
        AuthenticatePasswordRequest request = new AuthenticatePasswordRequest(encryptedPayload, secureRandom, getDHPublicKey(), connectedPublicKey);
        return request;
    }

    /**
     * @param loginRequest
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public byte[] receiveLoginRequest(AuthenticatePasswordRequest loginRequest) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        generateDHSharedSecret(loginRequest.connectedPublicKey());

        byte[] decryptedData = cipherAESAlgorithm.decryptData(loginRequest.cipheredData(), loginRequest.secureRandom(), dhAlgorithm.getSharedSecret());
        byte[] hashedPWD = Arrays.copyOfRange(decryptedData, 0, decryptedData.length - secureRandom.length);
        byte[] incrementedSecureRandom = Arrays.copyOfRange(decryptedData, hashedPWD.length, decryptedData.length);

        if (bytesToLong(incrementedSecureRandom) == bytesToLong(secureRandom) + 1) {
            return concatArrays(hashedPWD, loginRequest.secureRandom());
        } else {
            return null;
        }
    }
    public byte[] decryptData(byte[] data, byte[] secureRandom) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return cipherAESAlgorithm.decryptData(data, secureRandom, dhAlgorithm.getSharedSecret());
    }

    /**
     *
     * @param signedToken
     * @param incrementedRandom
     * @param rsaPublicKey
     * @return
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public byte[] confirmSuccessfulLogin(byte[] signedToken, byte[] plainToken, byte[] secret, byte[] incrementedRandom, byte[] rsaPublicKey) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IOException {

        AuthenticatePasswordResponse response = new AuthenticatePasswordResponse(signedToken, plainToken, incrementedRandom, rsaPublicKey);

        return cipherAESAlgorithm.encryptHashedData(serializeRecord(response), dhAlgorithm.getSharedSecret(), secret);
    }

    //
//    private static byte[] signToken(String token, byte[] secretKey)
//            throws NoSuchAlgorithmException, InvalidKeyException {
//        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
//        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
//        hmacSha256.init(secretKeySpec);
//
//        return hmacSha256.doFinal(token.getBytes(StandardCharsets.UTF_8));
//    }
    public static byte[] serializeRecord(AuthenticatePasswordResponse record) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(record);
        out.flush();
        return bos.toByteArray();
    }
}
