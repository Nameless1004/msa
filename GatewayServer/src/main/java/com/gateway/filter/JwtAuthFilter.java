package com.gateway.filter;

import io.jsonwebtoken.lang.Strings;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String uri = request.getURI().getPath();
            log.info("Request URI: {}", uri);

            if(uri.startsWith("/auth")) {
                return chain.filter(exchange);
            }

            String bearerToken = jwtUtil.getTokenFromHeader(request, config.headerName);

            if(!Strings.hasText(bearerToken) || !bearerToken.startsWith(config.granted + " ")) {
                log.warn("Access token is missing or invalid!!");
                return jwtUtil.onError(exchange, "No or Invalid AccessToken", HttpStatus.UNAUTHORIZED);
            }

            String token = bearerToken.substring(0, 7);
            try {
                var claims = jwtUtil.validateToken(token);
                var id = claims.getSubject();
                var email = claims.get("email", String.class);
                var role = claims.get("userRole", String.class);

                exchange.getRequest().mutate()
                        .header("X-User-Id",id)
                        .header("X-User-Email",email)
                        .header("X-User-Role",role)
                        .build();

                return chain.filter(exchange);
            } catch (RuntimeException e) {
                log.error("Token is invalid: {}", e.getMessage());
            }

            return jwtUtil.onError(exchange, "No or Invalid AccessToken", HttpStatus.UNAUTHORIZED);
        });
    }

    @Getter
    @Setter
    public static class Config {
        private String headerName;
        private String granted;
        private List<String> roles;
    }
}
