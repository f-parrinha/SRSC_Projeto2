package com.client.serviceClients;

import com.api.requests.authenticate.*;
import com.api.auth.SecureLogin;
import com.api.requests.*;
import com.client.AbstractClient;
import com.api.services.DispatcherService;
import org.springframework.http.HttpStatus;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.json.JsonObject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;


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

    public FDispatcherClient(URI uri, SSLContext sslContext, SSLParameters sslParameters) throws NoSuchAlgorithmException, KeyManagementException, NoSuchPaddingException, InvalidKeyException {
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
    }

    private HttpResponse<String> requestAccessControlToken(String username) throws IOException, InterruptedException {
        HttpRequest request = RestRequest.getInstance(baseUri, true).get("/access/{username}", authToken, username);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> requestDHPublicKey(String username) throws IOException, InterruptedException {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/init-connection/{username}", "", username);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> listFiles(String username) throws IOException, InterruptedException {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/ls/{username}", username);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> listFiles(String username, String path) throws IOException, InterruptedException {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/ls/{username}/{path}", username, path);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> makeDirectory(String username, MkDirRequest mkDirRequest) throws IOException, InterruptedException {
        JsonObject mkDirJson = mkDirRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).post("/mkdir/{username}", mkDirJson, username);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> put(String username, String path, String fileName) {
        /*@TODO:
         *  - Add File resource
         *  - Add request using that resource and place it on body
         */
        return null;
    }

    @Override
    public HttpResponse<String> get(String username, String path, String fileName) throws IOException, InterruptedException {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/get/{username}/{path}/{fileName}", username, path, fileName);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> copy(String username, CopyRequest copyRequest) throws IOException, InterruptedException {
        JsonObject copyJson = copyRequest.serialize();
        HttpRequest request = RestRequest.getInstance(baseUri).post("/cp/{username}", copyJson, username);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> remove(String username, String path, String fileName) throws IOException, InterruptedException {
        HttpRequest request = RestRequest.getInstance(baseUri).delete("/rm/{username}/{path}/{file}", username, path, fileName);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpResponse<String> file(String username, String path, String fileName) throws IOException, InterruptedException {
        HttpRequest request = RestRequest.getInstance(baseUri).get("/file/{username}/{path}/{file}", username, path, fileName);
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
