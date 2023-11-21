package com.client;

import com.client.shell.FClientShell;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
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
     * @param uri FServer URI
     */
    public AbstractClient(URI uri) throws SSLException {
        this.webClient = createWebClient(uri);
    }

    /**
     * Creates a WebClient using TLS protocol
     * @param uri FServer URI
     * @return Spring WebClient
     * @throws SSLException SSLContext errors
     */
    public WebClient createWebClient (URI uri) throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        HttpClient httpClient = HttpClient.create()
                .secure(t -> t.sslContext(sslContext))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
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