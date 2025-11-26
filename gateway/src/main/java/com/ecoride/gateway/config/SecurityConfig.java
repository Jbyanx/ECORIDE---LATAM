package com.ecoride.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF para facilitar pruebas API
                .authorizeExchange(exchanges -> exchanges
                        // Permitir acceso libre a endpoints de monitoreo y login
                        .pathMatchers("/actuator/**", "/login/**", "/error").permitAll()

                        // TODO LO DEMÁS requiere autenticación
                        .anyExchange().authenticated()
                )
                // 1. Habilita el Login con navegador (OIDC) - Lo que te redirige al HTML
                .oauth2Login(org.springframework.security.config.Customizer.withDefaults())

                // 2. ¡ESTO ES LO QUE FALTA! Habilita la validación de Tokens JWT (Postman/Mobile)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(org.springframework.security.config.Customizer.withDefaults()));

        return http.build();
    }
}