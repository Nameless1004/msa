package com.authserver.security;

import com.authserver.enums.TokenType;
import com.authserver.enums.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "Refresh-Token";
    public static final String REDIS_REFRESH_TOKEN_PREFIX = "Refresh_";

    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Value("${jwt.secret.token}")
    private String SECRET_KEY;
    private SecretKey key;

    @PostConstruct
    public void init() {
        key = getSecretKeyFromBase64(SECRET_KEY);
    }

    public String createAccessToken(Long userId, String email, UserRole role) {
        Date now = new Date();
        return BEARER_PREFIX + Jwts.builder()
                .claim("category", TokenType.ACCESS.name())
                .expiration(new Date(now.getTime() + TokenType.ACCESS.getLifeTime()))
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("userRole", role.getUserRole())
                .issuedAt(now)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long userId, String email,  UserRole role) {
        Date now = new Date();
        return BEARER_PREFIX + Jwts.builder()
                .claim("category", TokenType.REFRESH.name())
                .expiration(new Date(now.getTime() + TokenType.REFRESH.getLifeTime()))
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("userRole", role.getUserRole())
                .issuedAt(now)
                .signWith(key)
                .compact();
    }

    private SecretKey getSecretKeyFromBase64(String base64) {
        return Keys.hmacShaKeyFor(Base64Coder.decode(base64));
    }
}
