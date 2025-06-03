package com.ssg.order.domain.token;

public interface BlacklistHandler {
    boolean isBlacklisted(String token);
    void addToBlacklist(String token, long expirationTime);
}
