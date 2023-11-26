package com.client;

import com.client.shell.FClientShell;
import io.netty.handler.ssl.SslContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;


/**
 * Abstract Class  FClient  creates a base client used by the different implementations
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public abstract class AbstractClient {

    /** Constants */
    public static final String MEDIA_TYPE = "application/json";

    /** Variables */
    protected final WebClient webClient;

    /**
     * Constructor
     *
     * @param uri FServer URI
     */
    public AbstractClient(URI uri, SslContext sslContext) {
        this.webClient = createWebClient(uri, sslContext);
    }

    /**
     * Creates a WebClient using TLS protocol
     * @param uri FServer URI
     * @return Spring WebClient
     */
    public WebClient createWebClient (URI uri, SslContext sslContext) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext))))
                .baseUrl(uri.toString())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE)
                .defaultHeader(HttpHeaders.ACCEPT, MEDIA_TYPE)
                .build();
    }

    /**
     * Reads a response from the server and prints the correct results (errors and content)
     * Follows a subscriber/publisher pattern
     * @param response response from the server
     */
    public void readResponse(Mono<ResponseEntity<String>> response) {
        if (response == null) {
            FClientShell.printError("Response is null. Check if the request is being sent");
            return;
        }

        // Subscribe to check response status (subscriber/publisher pattern...)
        response.subscribe(
                result -> {
                    FClientShell.printResult(result.getBody());
                },
                error -> {
                    FClientShell.printResult(error.getMessage());
                },
                () -> { /* Leaving empty... */ }
        );
    }
}