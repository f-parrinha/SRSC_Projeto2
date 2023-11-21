package com.client;

import com.client.shell.FClientShell;
import io.netty.channel.ChannelOption;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;


/**
 * Abstract Class  FClient  creates a base client used by the different implementations
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public abstract class AbstractClient {

    /** Variables */
    protected final WebClient webClient;


    /**
     * Constructor
     * @param URL FServer URL
     */
    public AbstractClient(String URL) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();

    }

    /**
     * Reads a response from the server and prints the correct results (errors and content)
     * Follows a subscriber/publisher pattern
     * @param response reponse from the server
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