package com.client;

import com.api.common.shell.Shell;
import org.springframework.http.HttpStatus;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;


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

        String responseText = response.body();
        HttpStatus status = HttpStatus.resolve(response.statusCode());
        switch (Objects.requireNonNull(status)) {
            case OK -> Shell.printResult(responseText == null ? "Ok." : responseText);
            case NOT_FOUND -> Shell.printError(responseText == null ? "Not Found" : responseText);
            case BAD_REQUEST -> Shell.printError(responseText == null ? "Bad Request" : responseText);
            case FORBIDDEN -> Shell.printError(responseText == null ? "Forbidden" : responseText);
            case CONFLICT -> Shell.printError(responseText == null ? "Conflict in the interaction." : responseText);
            default -> Shell.printError("Unexpected value -> " + status);
        }
        Shell.printLine("");
    }

    /**
     * Sends a given request, if the destination is online
     * @param request request to send
     * @return response or null, if it is not possible to send the given request
     */
    protected HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            return null;    // Nothing to do here...
        }
    }
}