package com.ssg.order.infra.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.order.domain.cache.CacheStore;
import com.ssg.order.domain.common.annotation.exception.InternalServerException;
import com.ssg.order.domain.common.annotation.exception.code.InternalErrorCode;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisCacheStore implements CacheStore {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final static Duration DEFAULT_DURATION = Duration.ofMinutes(3);

    @Override
    public void put(String key, Object value) {
        put(
            key,
            value,
            DEFAULT_DURATION
        );
    }

    @Override
    public void put(String key, Object value, Duration expiryTime) {
        try {
            redisTemplate.opsForValue()
                .set(
                    key,
                    objectToString(value),
                    expiryTime
                );
        } catch (Exception e) {
            throw new InternalServerException(
                InternalErrorCode.REPLACE_SERVER_MESSAGE,
                "Failed to put value to redis :: " + e.getMessage()
            );
        }
    }

    @Override
    public String get(String key) {
        return get(
            key,
            String.class
        );
    }

    @Override
    public <T> T get(String key, Class<T> valueType) {
        try {
            return stringToObject(
                redisTemplate.opsForValue().get(key),
                valueType
            );
        } catch (Exception e) {
            throw new InternalServerException(
                InternalErrorCode.REPLACE_SERVER_MESSAGE,
                List.of("Failed to get value from redis :: " + e.getMessage())
            );
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            throw new InternalServerException(
                InternalErrorCode.REPLACE_SERVER_MESSAGE,
                List.of("Failed to check key existence in redis :: " + e.getMessage())
            );
        }
    }

    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            throw new InternalServerException(
                InternalErrorCode.REPLACE_SERVER_MESSAGE,
                List.of("Failed to delete key from redis :: " + e.getMessage())
            );
        }
    }

    private String objectToString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T stringToObject(
        String value,
        Class<T> valueType
    ) {
        if (value == null) {
            return null;
        }

        try {
            return objectMapper.readValue(
                value,
                valueType
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
