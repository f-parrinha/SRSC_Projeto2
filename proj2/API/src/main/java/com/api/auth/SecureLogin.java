package com.api.auth;

import com.api.utils.UtilsBase;
import com.api.requests.authenticate.AuthenticatePasswordRequest;
import com.api.requests.authenticate.AuthenticatePasswordResponse;
import com.api.rest.SingleDataRequest;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import static com.api.utils.UtilsBase.*;

public class SecureLogin {
    private static final short NONCE_SIZE = 16;
    private final DHAlgorithm dhAlgorithm;
    private final CipherAESAlgorithm cipherAESAlgorithm;
    private byte[] secureRandom;

    public SecureLogin() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
        dhAlgorithm = new DHAlgorithm();
        cipherAESAlgorithm = new CipherAESAlgorithm();
    }
    public byte[] getSecureRandom() {
        return secureRandom;
    }
    /**
     * @return
     */
    public void generateRandomBytes() {
        secureRandom = UtilsBase.generateRandomBytes(NONCE_SIZE);
    }

    /**
     * @return
     */
    public byte[] getDHPublicKey() {
        return dhAlgorithm.getPublicKey();
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
     * @param data
     * @return
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public byte[] decryptData(byte[] data) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return cipherAESAlgorithm.decryptData(data, secureRandom, dhAlgorithm.getSharedSecret());
    }

    /**
     * Generates the final request for authentication, in the form:
     * { H (PWD) || SecureRandom1+1 } Ks || username || SecureRandom2 || Assinatura de Ycliente
     * where:
     * - Ks is the DH shared secret between the client and the FAuth module
     * - SecureRandom are used to prevent reply attacks
     *
     * @param pwd               Client password
     * @param username          Client username
     * @param serversRandom     Random
     * @param receivedPublicKey Client DH Public Key
     * @return Request
     */
    public AuthenticatePasswordRequest formLoginRequest(String pwd, byte[] serversRandom, byte[] receivedPublicKey) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        try {
            generateDHSharedSecret(receivedPublicKey);
            byte[] incrementedRandom = incrementByteArray(serversRandom);
            generateRandomBytes();

            byte[] hashedPWD = cipherAESAlgorithm.hashData(pwd.getBytes());
            byte[] data = concatArrays(hashedPWD, incrementedRandom);
            byte[] encryptedPayload = cipherAESAlgorithm.encryptHashedData(data, dhAlgorithm.getSharedSecret(), secureRandom);

            return new AuthenticatePasswordRequest(encryptedPayload, secureRandom, getDHPublicKey());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

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

        generateDHSharedSecret(loginRequest.publicKey());

        byte[] decryptedData = cipherAESAlgorithm.decryptData(loginRequest.cipheredData(), loginRequest.secureRandom(), dhAlgorithm.getSharedSecret());
        byte[] hashedPWD = Arrays.copyOfRange(decryptedData, 0, decryptedData.length - secureRandom.length);
        byte[] incrementedSecureRandom = Arrays.copyOfRange(decryptedData, hashedPWD.length, decryptedData.length);

        if (bytesToLong(incrementedSecureRandom) != bytesToLong(secureRandom) + 1)
            return null;

        return hashedPWD;
    }

    /**
     * @param token
     * @param secret
     * @param secureRandom
     * @return
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public String confirmSuccessfulLogin(String token, byte[] secret, byte[] secureRandom) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IOException {

        byte[] incrementedRandom = UtilsBase.incrementByteArray(secureRandom);
        AuthenticatePasswordResponse response = new AuthenticatePasswordResponse(token, incrementedRandom);

        byte[] cipheredResponse = cipherAESAlgorithm.encryptHashedData(response.serialize().toString().getBytes(), dhAlgorithm.getSharedSecret(), secret);

        return new SingleDataRequest(cipheredResponse).serialize().toString();

    }

}
