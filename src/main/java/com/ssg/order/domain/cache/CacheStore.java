package com.ssg.order.domain.cache;

import java.time.Duration;

public interface CacheStore {
    void put(
        String key,
        Object value
    );

    /**
     * expiryTime 만큼 캐시를 저장한다.
     */
    void put(
        String key,
        Object value,
        Duration expiryTime
    );

    String get(String key);

    <T> T get(
        String key,
        Class<T> valueType
    );

    boolean exists(String key);

    void delete(String key);

    boolean lock(
        String key,
        String value,
        Duration duration
    );
}
