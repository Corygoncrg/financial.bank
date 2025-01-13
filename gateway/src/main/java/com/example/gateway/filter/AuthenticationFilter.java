package com.example.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Order(1)
@Component
public class AuthenticationFilter implements GlobalFilter {

    private final WebClient.Builder webClientBuilder;
    private static final List<String> EXCLUDED_PATHS = List.of("/login", "/validate"); // Exclude these paths

    @Value("${http.host}")
    private String localhost;

    public AuthenticationFilter(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        // Skip authentication for excluded paths
        if (EXCLUDED_PATHS.stream().anyMatch(excluded -> path.matches(excluded + "(/.*)?"))) {
            System.out.println("Excluding path from authentication: " + path);
            return chain.filter(exchange); // Continue without authentication
        }

        // Validate Authorization header
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.err.println("Missing or invalid Authorization header");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete(); // Reject the request
        }

        String token = authHeader.substring(7); // Extract token

        // Validate token with the security service
        return webClientBuilder.build()
                .post()
                .uri("http://" + localhost + ":8082/validate")
                .bodyValue(token)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> {
                    System.err.println("Failed to validate token. Status: " + clientResponse.statusCode());
                    return Mono.error(new RuntimeException("Unauthorized"));
                })
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500))) // Retry validation up to 3 times
                .doOnError(error -> System.err.println("Validation error: " + error.getMessage()))
                .then(chain.filter(exchange)); // Proceed if validation succeeds
    }
}
