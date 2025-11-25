package com.ecoride.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication())
                .flatMap(authentication -> {

                    String extractedId = null;

                    if (authentication.getPrincipal() instanceof OidcUser) {
                        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
                        extractedId = oidcUser.getPreferredUsername();
                    } else if (authentication instanceof JwtAuthenticationToken) {
                        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authentication;
                        extractedId = jwtToken.getToken().getClaimAsString("preferred_username");
                    }

                    final String userId = extractedId;

                    if (userId != null) {
                        // --- SOLUCIÓN ROBUSTA CON DECORADOR ---

                        // 1. Creamos un decorador que envuelve la petición original
                        ServerHttpRequestDecorator requestDecorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                            @Override
                            public HttpHeaders getHeaders() {
                                // 2. Creamos una copia NUEVA y EDITABLE de los headers
                                HttpHeaders headers = new HttpHeaders();
                                headers.putAll(super.getHeaders());

                                // 3. Agregamos nuestro header con seguridad
                                headers.add("X-User-Id", userId);
                                return headers;
                            }
                        };

                        // 4. Continuamos la cadena usando la petición decorada
                        return chain.filter(exchange.mutate().request(requestDecorator).build());
                    }

                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }
}