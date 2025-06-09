package com.ssg.order.infrastructure.jwt;

import com.ssg.order.domain.token.BlacklistHandler;
import com.ssg.order.domain.token.TokenHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtService implements TokenHandler {
    @Value("${security.jwt.key}")
    private String secret;

    private final BlacklistHandler blacklistHandler;

    @Override
    public String generateToken(String name, long expirationMs) {
        return Jwts.builder()
            .setSubject(name)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    @Override
    public String getIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            if (blacklistHandler.isBlacklisted(token)) {
                return false;
            }

            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public long getExpirationTimeInSeconds(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

            Date expiration = claims.getExpiration();
            long currentTime = System.currentTimeMillis();
            long expirationTime = expiration.getTime();

            return Math.max(0, (expirationTime - currentTime) / 1000);
        } catch (JwtException | IllegalArgumentException e) {
            return 0;
        }
    }

    @Override
    public void invalidateToken(String token) {
        long expirationTime = getExpirationTimeInSeconds(token);
        if (expirationTime > 0) {
            blacklistHandler.addToBlacklist(token, expirationTime);
        }
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}