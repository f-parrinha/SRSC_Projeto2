package com.client.serviceClients;

import com.api.AuthenticatePasswordRequest;
import com.api.AuthenticateUsernameResponse;
import com.api.SecureLogin;
import com.client.AbstractClient;
import com.api.services.FServerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import static com.api.common.UtilsBase.incrementByteArray;
import static com.api.common.UtilsBase.toHex;

/**
 * Class  FDispatcherClient  offers tools to create requests to the FServer (Dispatcher module)
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public class FClient extends AbstractClient implements FServerService {
    private SecureLogin secureLogin;

    public FClient(String URL) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
        super(URL);
        secureLogin = new SecureLogin();
    }

    @Override
    public Mono<ResponseEntity<String>> login(String username, String password) {

        return requestDHPublicKey(username)
                .flatMap(responseEntity -> {

                    if (responseEntity.getStatusCode() == HttpStatusCode.valueOf(200)) {

                        AuthenticateUsernameResponse response = responseEntity.getBody();

                        byte[] serversRandom = response.secureRandom();
                        byte[] receivedPublicKey = response.publicKey();
                        byte[] receivedConnectedPublicKey = response.connectedPublicKey();

                        try {
                            byte[] key = secureLogin.calculateKey(receivedPublicKey);
                            secureLogin.generateDHSharedSecret(receivedConnectedPublicKey);
                            byte[] incrementedRandom = incrementByteArray(serversRandom);
                            byte[] newSecureRandom = secureLogin.generateRandomBytes();

                            AuthenticatePasswordRequest request = secureLogin.formLoginRequest(password, incrementedRandom, key);

                            return webClient.post()
                                    .uri("/login")
                                    .body(Mono.just(request), AuthenticatePasswordRequest.class)
                                    .retrieve()
                                    .toEntity(String.class);

                        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException |
                                 InvalidAlgorithmParameterException | IllegalBlockSizeException |
                                 BadPaddingException e) {
                            // Handle cryptographic exceptions
                            return Mono.error(e);
                        }
                    }
                    if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
                        // Return a response entity indicating user not found
                        return Mono.just(ResponseEntity.ofNullable("User not found!"));
                    } else {
                        // Handle other 4xx errors or rethrow the exception
                        // You might want to log the exception details for debugging
                        return Mono.error(new RuntimeException("Unexpected response: " + responseEntity.getStatusCode()));
                    }
                });

    }

    @Override
    public Mono<ResponseEntity<AuthenticateUsernameResponse>> requestDHPublicKey(String username) {
        return webClient.post()
                .uri("/init-connection")
                .body(Mono.just(username), String.class)
                .retrieve()
                .toEntity(AuthenticateUsernameResponse.class);
    }

    @Override
    public Mono<ResponseEntity<String>> listFiles(String username) {
        return webClient.get()
                .uri("/ls/{username}", username)
                .retrieve()
                .toEntity(String.class);
    }

    @Override
    public Mono<ResponseEntity<String>> listFiles(String username, String path) {
        return webClient.get()
                .uri("/ls/{username}/{path}", username, path)
                .retrieve()
                .toEntity(String.class);
    }

    @Override
    public Mono<ResponseEntity<String>> makeDirectory(String username, String path) {
        return webClient.post()
                .uri("/mkdir/{username}/{path}", username, path)
                .retrieve()
                .toEntity(String.class);
    }

    @Override
    public Mono<ResponseEntity<String>> put(String username, String path, String fileName) {
        /*@TODO:
         *  - Add File resource
         *  - Add request using that resource and place it on body
         */
        return null;
    }

    @Override
    public Mono<ResponseEntity<String>> get(String username, String path, String fileName) {
        /*@TODO*/
        return null;
    }

    @Override
    public Mono<ResponseEntity<String>> copy(String username, String sourcePath, String sourceFile, String destPath, String destFile) {
        return webClient.post()
                .uri("/cp/{username}/{sourcePath}/{sourceFile}/{destPath}/{destFile}",
                        username, sourcePath, sourceFile, destPath, destFile)
                .retrieve()
                .toEntity(String.class);
    }

    @Override
    public Mono<ResponseEntity<String>> remove(String username, String path, String fileName) {
        return webClient.delete()
                .uri("/rm/{username}/{path}/{file}", username, path, fileName)
                .retrieve()
                .toEntity(String.class);
    }

    @Override
    public Mono<ResponseEntity<String>> file(String username, String path, String fileName) {
        return webClient.get()
                .uri("/file/{username}/{path}/{file}", username, path, fileName)
                .retrieve()
                .toEntity(String.class);
    }

}
