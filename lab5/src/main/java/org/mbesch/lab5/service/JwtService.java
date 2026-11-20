package org.mbesch.lab5.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.mbesch.lab5.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final SecretKey secretKey;
    private final long accessExpirationSeconds;
    private final long refreshExpirationSeconds;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration-seconds}") long accessExpirationSeconds,
            @Value("${jwt.refresh-expiration-seconds}") long refreshExpirationSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationSeconds = accessExpirationSeconds;
        this.refreshExpirationSeconds = refreshExpirationSeconds;
    }

    public record GeneratedToken(String token, Instant expiresAt) {}

    public GeneratedToken generateAccessToken(User user) {
        return generateToken(user, "access", accessExpirationSeconds);
    }

    public GeneratedToken generateRefreshToken(User user) {
        return generateToken(user, "refresh", refreshExpirationSeconds);
    }

    public Claims validateAccessToken(String token) {
        return validateToken(token, "access");
    }

    public Claims validateRefreshToken(String token) {
        return validateToken(token, "refresh");
    }

    private GeneratedToken generateToken(User user, String type, long expirationSeconds) {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant expiresAt = now.plusSeconds(expirationSeconds);
        String token = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getEmail())
                .claim("type", type)
                .claim("role", user.getRole())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
        return new GeneratedToken(token, expiresAt);
    }

    private Claims validateToken(String token, String expectedType) {
        if (token == null || token.isBlank()) {
            throw new JwtAuthException("Token is missing");
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String subject = claims.getSubject();
            String jti = claims.getId();
            Date issuedAt = claims.getIssuedAt();
            Date expiration = claims.getExpiration();
            String type = claims.get("type", String.class);

            if (subject == null || subject.isBlank()
                    || jti == null || jti.isBlank()
                    || issuedAt == null
                    || expiration == null
                    || !expectedType.equals(type)) {
                throw new JwtAuthException("Invalid token claims or type");
            }
            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthException("Invalid or expired token");
        }
    }
}
