package com.client;

import com.api.common.shell.Shell;
import org.springframework.http.HttpStatus;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


/**
 * Abstract Class  FClient  creates a base client used by the different implementations
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public abstract class AbstractClient {

    /** Constants */
    public static final Duration TIMEOUT = Duration.ofSeconds(10);
    protected final HttpClient client;
    protected final URI baseUri;

    /**
     * Constructor
     * @param uri FServer URI
     * @param sslContext custom SSLContext object to configure TLS communication
     * @param sslParameters custom SSLParameters object to configure TLS communication
     */
    public AbstractClient(URI uri, SSLContext sslContext, SSLParameters sslParameters) {
        this.client = createHttpsClient(sslContext, sslParameters);
        this.baseUri = uri;
    }

    /**
     * Creates a HTTPS client (TLS protocol) with custom SSL configurations
     * @param sslContext custom SSLContext object to configure TLS communication
     * @param sslParameters custom SSLParameters object to configure TLS communication
     * @return HTTPS client with custom SSL configs
     */
    public HttpClient createHttpsClient(SSLContext sslContext, SSLParameters sslParameters) {
        return HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .sslParameters(sslParameters)
                .sslContext(sslContext)
                .build();
    }

    /**
     * Reads a response from the server and prints the correct results (errors and content)
     * Follows a subscriber/publisher pattern
     * @param response response from the server
     */
    public void readResponse(HttpResponse<String> response) {
        if (response == null) {
            Shell.printError("Response is null. Check if the request is being sent");
            return;
        }

        HttpStatus status = HttpStatus.resolve(response.statusCode());
        switch (Objects.requireNonNull(status)) {
            case OK -> Shell.printResult(response.body());
            case NOT_FOUND -> Shell.printError("Not Found");
            case BAD_REQUEST -> Shell.printError("Bad Request");
            case FORBIDDEN -> Shell.printError("Forbidden");
            default -> Shell.printError("Unexpected value -> " + status);
        }
    }

}