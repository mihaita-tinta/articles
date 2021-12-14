package com.mih.messenger;

import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class CsrfHeaderWriterWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Mono<CsrfToken> token = (Mono<CsrfToken>) exchange.getAttributes().get(CsrfToken.class.getName());
        if (token != null) {
            return token.flatMap(t -> chain.filter(exchange));
        }
        return chain.filter(exchange);
    }
}
