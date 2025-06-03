package com.ssg.order.infra.jwt.util;

import com.ssg.order.infra.jwt.BlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${security.jwt.key}")
    private String secret;

    private final BlacklistService blacklistService;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String name, long expirationMs) {
        return Jwts.builder()
            .setSubject(name)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public String getIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            if (blacklistService.isBlacklisted(token)) {
                return false;
            }

            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

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

    public void invalidateToken(String token) {
        long expirationTime = getExpirationTimeInSeconds(token);
        if (expirationTime > 0) {
            blacklistService.addToBlacklist(token, expirationTime);
        }
    }
}