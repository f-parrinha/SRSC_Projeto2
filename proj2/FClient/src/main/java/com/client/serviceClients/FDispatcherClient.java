package com.client.serviceClients;

import com.api.auth.SecureLogin;
import com.api.rest.RestRequest;
import com.api.rest.requests.CopyRequest;
import com.api.rest.requests.MkDirRequest;
import com.api.rest.requests.PutRequest;
import com.api.rest.requests.authenticate.*;
import com.client.AbstractClient;
import com.api.rest.requests.LoginRequest;
import com.api.services.DispatcherService;
import org.springframework.http.HttpStatus;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.json.JsonObject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * Class  FDispatcherClient  offers tools to create requests to the FServer (Dispatcher module)
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */

public class FDispatcherClient extends AbstractClient implements DispatcherService<HttpResponse<String>> {
    private final SecureLogin secureLogin;
    private String authToken = "";
    private String accessToken = "";

    public FDispatcherClient(URI uri, SSLContext sslContext, SSLParameters sslParameters)  {
        super(uri, sslContext, sslParameters);
        secureLogin = new SecureLogin();
    }


    @Override
    public HttpResponse<String> login(String username, String password) throws IOException, InterruptedException {

        HttpResponse<String> responseEntity = requestDHPublicKey(username);
        HttpStatus status = HttpStatus.resolve(responseEntity.statusCode());

        if (status == null || !status.is2xxSuccessful())
            return responseEntity;

        AuthenticateUsernameResponse response = AuthenticateUsernameResponse.fromJsonString(responseEntity.body());
        return createSecureLoginRequest(loginReq, response);
    }

    @Override
    public HttpResponse<String> requestDHPublicKey(String username) throws IOException, InterruptedException {
        RequestKeyExchange requestKeyExchange = new RequestKeyExchange(username);
        JsonObject requestKeyExchangeJson = requestKeyExchange.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).post("/init-connection", requestKeyExchangeJson);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> listFiles(String username) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/ls/{username}", username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> listFiles(String username, String path) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/ls/{username}/{path}", username, path);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> makeDirectory(String username, MkDirRequest mkDirRequest) {
        JsonObject json = mkDirRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).post("/mkdir/{username}", json, username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> put(String username, PutRequest putRequest) {
        JsonObject json = putRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).put("/put/{username}", json, username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> get(String username, String path) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/get/{username}/{path}", username, path);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> copy(String username, CopyRequest copyRequest) {
        JsonObject json = copyRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).put("/cp/{username}", json, username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> remove(String username, String path) {
        HttpRequest request = RestRequest.getInstance(baseUri).delete("/rm/{username}/{path}", username, path);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> file(String username, String path) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/file/{username}/{path}", username, path);
        return sendRequest(request);
    }

    private HttpResponse<String> createSecureLoginRequest(String username, String password, AuthenticateUsernameResponse response) {
        try {
            // Create request
            AuthenticatePasswordRequest loginRequest = secureLogin.formLoginRequest(password, response.secureRandom(), response.publicKey());
            JsonObject loginJson = loginRequest.serialize();
            HttpRequest request = RestRequest.getInstance(baseUri).post("/login", authToken, loginJson);

            var responseEntity = sendRequest(request);
            var status = HttpStatus.resolve(responseEntity.statusCode());

            // Check if status is not ok
            if (status == null || !status.is2xxSuccessful()) {
                return responseEntity;
            }

            // Request was successful
            SuccessfullAuthenticationResponse resp = SuccessfullAuthenticationResponse.fromJsonString(responseEntity.body());

            byte[] plainData = secureLogin.decryptData(resp.encryptedData());
            String jsonString = new String(plainData, StandardCharsets.UTF_8);

            AuthenticatePasswordResponse pwdResp = AuthenticatePasswordResponse.fromJsonString(jsonString);
            authToken = pwdResp.token();
            return responseEntity;

        } catch (InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException |
                 BadPaddingException e) {
            return null;
        }
        //////
        try {

            AuthenticatePasswordRequest loginRequest = secureLogin.formLoginRequest(password, response.secureRandom(), response.publicKey());
            JsonObject loginJson = loginRequest.serialize();

            HttpRequest request = RestRequest.getInstance(baseUri).post("/login/{username}", loginJson, username);
            responseEntity = client.send(request, HttpResponse.BodyHandlers.ofString());
            status = HttpStatus.resolve(responseEntity.statusCode());

            if (status != null && status.is2xxSuccessful()) {
                SingleDataRequest resp = SingleDataRequest.fromJsonString(responseEntity.body());

                byte[] plainData = secureLogin.decryptData(resp.data());
                String jsonString = new String(plainData, StandardCharsets.UTF_8);

                AuthenticatePasswordResponse pwdResp = AuthenticatePasswordResponse.fromJsonString(jsonString);
                authToken = pwdResp.token();

                HttpResponse<String> accessControlToken = requestAccessControlToken(username);
                HttpStatus httpStatus = HttpStatus.resolve(accessControlToken.statusCode());

                if (httpStatus != null && httpStatus.is2xxSuccessful()) accessToken = accessControlToken.body();

            }

            return responseEntity;
        } catch (InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException |
                 BadPaddingException e) {
            return null;
        }
        ////////
    }
}

