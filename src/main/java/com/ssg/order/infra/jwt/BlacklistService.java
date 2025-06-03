package com.ssg.order.infra.jwt;

import static com.ssg.order.domain.common.annotation.constants.CommonConstants.JWT_BLACKLIST_TOKEN_PREFIX;

import com.ssg.order.domain.cache.CacheStore;
import com.ssg.order.domain.token.BlacklistHandler;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlacklistService implements BlacklistHandler {
    private final CacheStore cacheStore;

    @Override
    public boolean isBlacklisted(String token) {
        String key = JWT_BLACKLIST_TOKEN_PREFIX + token;
        return cacheStore.exists(key);
    }

    @Override
    public void addToBlacklist(String token, long expirationTime) {
        String key = JWT_BLACKLIST_TOKEN_PREFIX + token;
        cacheStore.put(key, "blacklisted", Duration.ofSeconds(expirationTime));
    }
}
