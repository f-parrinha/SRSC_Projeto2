package server.mainDispatcher;

import client.webClient.WebClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import server.FServer;

@RestController
public class FServerDispatcher extends FServer {

    private final WebClient authService;
    private final WebClient accessControlService;
    private final WebClient storageService;

    @Autowired
    public FServerDispatcher() {
        this.authService = WebClientService.createWebClient("http://localhost:8081");
        this.accessControlService = WebClientService.createWebClient("http://localhost:8082");
        this.storageService = WebClientService.createWebClient("http://localhost:8083");
    }


    @GetMapping("/helloWorld")
    public Mono<String> getHelloWorld() {
        return Mono.just("Hello World");
    }


}
