package com.example.groupProject.config.util;

import com.example.groupProject.config.websocket.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {
    private static final String JWT_EXPIRED_MESSAGE = "만료된 JWT 토큰입니다.";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_PREFIX = "Authorization";

    private final SecretKey secretKey;

    public JWTUtil(@Value("${jwt.secret})") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS512.key().build().getAlgorithm());
    }

    //검증
    public String getAccount(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("account", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public Boolean isExpired(String token) {
        boolean result = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
        return result;
    }

    //발급
    public String createJwt(String category, String account, String role, Long expiredMs) {
        String token = Jwts.builder()
                .claim("category", category)
                .claim("account", account)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
        return token;
    }

    public String extractJwt(final StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader(AUTHORIZATION_PREFIX);
        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }
        return authorization;
    }

    public void validateToken(final String authorization) {
        try {
            isExpired(authorization);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(JWT_EXPIRED_MESSAGE);
        }
    }
}
