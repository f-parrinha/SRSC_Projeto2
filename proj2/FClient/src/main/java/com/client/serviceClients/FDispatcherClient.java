package com.client.serviceClients;

import com.api.auth.SecureLogin;
import com.api.rest.RestRequest;
import com.api.rest.requests.CopyRequest;
import com.api.rest.requests.MkDirRequest;
import com.api.rest.requests.PutRequest;
import com.api.rest.requests.SingleDataRequest;
import com.api.rest.requests.authenticate.*;
import com.api.utils.JwtTokenUtil;
import com.client.AbstractClient;
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
    public HttpResponse<String> login(String username, String password) {

        HttpResponse<String> responseEntity = requestDHPublicKey(username);
        HttpStatus status = HttpStatus.resolve(responseEntity.statusCode());

        if (status == null || !status.is2xxSuccessful())
            return responseEntity;

        AuthenticateUsernameResponse response = AuthenticateUsernameResponse.fromJsonString(responseEntity.body());
        return createSecureLoginRequest(username, password, response);
    }

    @Override
    public HttpResponse<String> requestDHPublicKey(String username) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/init-connection/{username}", JwtTokenUtil.NO_TOKEN, JwtTokenUtil.NO_TOKEN, username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> listFiles(String username, String authHeader, String accessHeader) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/ls/{username}", authToken, accessToken, username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> listFiles(String username, String path, String authHeader, String accessHeader) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/ls/{username}/{path}", authToken, accessToken, username, path);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> makeDirectory(String username, MkDirRequest mkDirRequest,String authHeader, String accessHeader) {
        JsonObject json = mkDirRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).post("/mkdir/{username}", authToken, accessToken, json, username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> put(String username, PutRequest putRequest,String authHeader, String accessHeader) {
        JsonObject json = putRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).put("/put/{username}", authToken, accessToken, json, username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> get(String username, String path,String authHeader, String accessHeader) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/get/{username}/{path}", authToken, accessToken, username, path);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> copy(String username, CopyRequest copyRequest,String authHeader, String accessHeader) {
        JsonObject json = copyRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).put("/cp/{username}", authToken, accessToken, json, username);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> remove(String username, String path,String authHeader, String accessHeader) {
        HttpRequest request = RestRequest.getInstance(baseUri).delete("/rm/{username}/{path}", authToken, accessToken, username, path);
        return sendRequest(request);
    }

    @Override
    public HttpResponse<String> file(String username, String path,String authHeader, String accessHeader) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/file/{username}/{path}", authToken, accessToken, username, path);
        return sendRequest(request);
    }

    /**
     * Creates a secure login request by getting auth tokens and access tokens
     * @param username username to log in
     * @param password user's password
     * @param response Authenticate Username response
     * @return HttpResponse for the request
     */
    private HttpResponse<String> createSecureLoginRequest(String username, String password, AuthenticateUsernameResponse response) {
        try {
            // Create Auth request
            AuthenticatePasswordRequest loginRequest = secureLogin.formLoginRequest(password, response.secureRandom(), response.publicKey());
            JsonObject loginJson = loginRequest.serialize();
            HttpRequest request = RestRequest.getInstance(baseUri).post("/login/{username}", authToken, accessToken, loginJson, username);

            var responseEntity = sendRequest(request);
            var status = HttpStatus.resolve(responseEntity.statusCode());

            // Check if status is not ok
            if (status == null || !status.is2xxSuccessful()) {
                return responseEntity;
            }

            // Auth request was successful
            SingleDataRequest resp = SingleDataRequest.fromJsonString(responseEntity.body());

            byte[] plainData = secureLogin.decryptData(resp.data());
            String jsonString = new String(plainData, StandardCharsets.UTF_8);

            AuthenticatePasswordResponse pwdResp = AuthenticatePasswordResponse.fromJsonString(jsonString);
            authToken = pwdResp.token();

            // Create Access request
            System.out.println("TEST"+username);
            HttpResponse<String> accessControlToken = requestAccessControlToken(username);
            HttpStatus httpStatus = HttpStatus.resolve(accessControlToken.statusCode());

            // Check if Access request failed
            if (httpStatus == null || !httpStatus.is2xxSuccessful()) {
                return responseEntity;
            }

            accessToken = accessControlToken.body();
            System.out.println(accessToken);
            return responseEntity;

        } catch (InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException |
                 BadPaddingException e) {
            return null;
        }
    }

    private HttpResponse<String> requestAccessControlToken(String username) {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/access/{username}", authToken, accessToken, username);
        return sendRequest(request);
    }
}

