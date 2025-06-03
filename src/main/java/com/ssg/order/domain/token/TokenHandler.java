package com.ssg.order.domain.token;

public interface TokenHandler {
    String generateToken(String name, long expirationMs);
    String getIdFromToken(String token);
    boolean validateToken(String token);
    long getExpirationTimeInSeconds(String token);
    void invalidateToken(String token);
}
