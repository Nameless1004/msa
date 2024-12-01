package com.gateway.filter;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Value("${jwt.secret.token}")
    private String SECRET_KEY;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = getSecretKeyFromBase64(SECRET_KEY);
        System.out.println("SECRET_KEY = " + SECRET_KEY);
    }

    public Claims validateToken(String token) {
        try {
            return (Claims) Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        } catch (SecurityException | MalformedJwtException e) {
            throw new SecurityException(e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), e.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getTokenFromHeader(ServerHttpRequest request, String header) {
        return request.getHeaders().getFirst(header);
    }

    public Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    private SecretKey getSecretKeyFromBase64(String base64) {
        return Keys.hmacShaKeyFor(Base64Coder.decode(base64));
    }
}
