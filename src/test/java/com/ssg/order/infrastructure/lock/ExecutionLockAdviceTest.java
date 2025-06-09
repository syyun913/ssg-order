package com.ssg.order.infrastructure.lock;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CONFLICT;

import com.ssg.order.domain.cache.CacheStore;
import com.ssg.order.domain.common.annotation.exception.InternalServerException;
import com.ssg.order.domain.common.annotation.exception.code.InternalErrorCode;
import com.ssg.order.domain.common.util.lock.ExecutionLock;
import java.time.Duration;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("락 수행 어드바이스 테스트")
@ExtendWith(MockitoExtension.class)
class ExecutionLockAdviceTest {
    @Mock
    private CacheStore cacheStore;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private ExecutionLock executionLock;

    @InjectMocks
    private ExecutionLockAdvice executionLockAdvice;

    @Test
    @DisplayName("락을 성공적으로 획득한 경우 메서드가 정상 실행된다.")
    public void successWhenLockIsAcquired() throws Throwable {
        // given
        setupMockExecution(
            "testKey",
            "testPrefix",
            "testValue",
            1000L
        );
        when(cacheStore.lock(
            anyString(),
            anyString(),
            any(Duration.class)
        )).thenReturn(true);
        when(joinPoint.proceed()).thenReturn("Success");

        // when
        Object result = executionLockAdvice.handleExecutionLock(
            joinPoint,
            executionLock
        );

        // then
        assertAll(
            () -> assertEquals(
                "Success",
                result
            ),
            () -> verify(
                cacheStore,
                times(1)
            ).lock(
                "testPrefixtestKey",
                "testValue",
                Duration.ofMillis(1000)
            ),
            () -> verify(
                cacheStore,
                times(1)
            ).delete("testPrefixtestKey")
        );
    }

    @Test
    @DisplayName("락을 획득하지 못한 경우 오류 발생")
    public void exceptionWhenLockNotAcquired() {
        // given
        setupMockExecution(
            "testKey",
            "testPrefix",
            "testValue",
            1000L
        );
        when(cacheStore.lock(
            anyString(),
            anyString(),
            any(Duration.class)
        )).thenReturn(false);

        // when
        InternalServerException exception = assertThrows(
            InternalServerException.class,
            () -> executionLockAdvice.handleExecutionLock(
                joinPoint,
                executionLock
            )
        );

        // then
        assertAll(
            () -> assertEquals(
                InternalErrorCode.ALREADY_PROGRESS,
                exception.getErrorCode()
            ),
            () -> assertEquals(
                CONFLICT,
                exception.getErrorCode().getHttpStatus()
            ),
            () -> assertEquals(
                "이미 처리중인 요청입니다.",
                exception.getMessage()
            )
        );
    }

    @Test
    @DisplayName("메서드 실행 중 예외 발생 시 락 해제")
    public void unlockWhenExceptionThrownDuringMethodExecution() throws Throwable {
        // given
        setupMockExecution(
            "testKey",
            "testPrefix",
            "testValue",
            1000L
        );
        when(cacheStore.lock(
            anyString(),
            anyString(),
            any(Duration.class)
        )).thenReturn(true);
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Execution Error"));

        // when
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> {
                executionLockAdvice.handleExecutionLock(
                    joinPoint,
                    executionLock
                );
            }
        );

        // then
        assertAll(
            () -> assertEquals(
                "Execution Error",
                exception.getMessage()
            ),
            () -> verify(
                cacheStore,
                times(1)
            ).delete("testPrefixtestKey")
        );
    }

    @Test
    @DisplayName("Redis 오류 발생 시 메서드가 정상 실행된다.")
    public void successWhenRedisErrorOccurs() throws Throwable {
        // given
        setupMockExecution(
            "testKey",
            "testPrefix",
            "testValue",
            1000L
        );
        when(cacheStore.lock(
            anyString(),
            anyString(),
            any(Duration.class)
        )).thenThrow(new RuntimeException("Redis Error"));
        when(joinPoint.proceed()).thenReturn("Success");

        // when
        Object result = executionLockAdvice.handleExecutionLock(
            joinPoint,
            executionLock
        );

        // then
        assertAll(
            () -> assertEquals(
                "Success",
                result
            )
        );
    }

    private void setupMockExecution(
        String key,
        String prefix,
        String value,
        long lockTimeMs
    ) {
        when(executionLock.keyVariable()).thenReturn("'" + key + "'");
        when(executionLock.keyPrefix()).thenReturn(prefix);
        when(executionLock.value()).thenReturn(value);
        when(executionLock.lockTimeMs()).thenReturn(lockTimeMs);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"param1"});
        when(joinPoint.getArgs()).thenReturn(new Object[]{"argValue"});
    }
}