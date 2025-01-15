package com.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Order(0)
@Component
public class RewriteUsersPathFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        if (path.startsWith("/login") || path.startsWith("/validate") || path.startsWith("/transactions") || path.startsWith("/actuator")) {
            return chain.filter(exchange);
        }

        String newPath = path.startsWith("/users") ? path : "/users" + path;
        if (newPath.equals("/users/")) {
            newPath = "/users";
        }

        // Update the exchange with the rewritten path
        exchange = exchange.mutate().request(
                exchange.getRequest().mutate().path(newPath).build()
        ).build();

        return chain.filter(exchange);
    }
}
