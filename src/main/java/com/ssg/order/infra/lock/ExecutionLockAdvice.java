package com.ssg.order.infra.lock;

import com.ssg.order.domain.cache.CacheStore;
import com.ssg.order.domain.common.annotation.exception.InternalServerException;
import com.ssg.order.domain.common.annotation.exception.code.InternalErrorCode;
import com.ssg.order.domain.common.util.ExecutionLock;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ExecutionLockAdvice {
    private final CacheStore cacheStore;

    @Around("@annotation(executionLock)")
    public Object handleExecutionLock(
        ProceedingJoinPoint joinPoint,
        ExecutionLock executionLock
    ) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        ExpressionParser parser = new SpelExpressionParser(); // SpEL 표현식 parser 생성
        EvaluationContext context = new StandardEvaluationContext(); // 평가 컨텍스트 생성

        // 메서드 파라미터들을 SpEL 표현식에 사용할 수 있도록 컨텍스트에 등록
        for (int i = 0; i < signature.getParameterNames().length; i++) {
            context.setVariable(
                signature.getParameterNames()[i],
                joinPoint.getArgs()[i]
            );
        }

        String keyVariable = executionLock.keyVariable();
        String keyValue = (String) parser.parseExpression(keyVariable).getValue(context);
        String keyPrefix = executionLock.keyPrefix();

        String key = keyPrefix + keyValue;
        String value = executionLock.value();
        Duration lockTime = Duration.ofMillis(executionLock.lockTimeMs());
        boolean isLocked = false;

        try {
            // Redis 락 설정
            isLocked = lock(
                key,
                value,
                lockTime
            );

            if (!isLocked) {
                throw new InternalServerException(
                    InternalErrorCode.ALREADY_PROGRESS,
                    String.format(
                        "already progress: key: %s, value: %s, lockTime: %s ms",
                        key,
                        value,
                        lockTime.toMillis()
                    )
                );
            }

            // 실제 메서드를 실행
            return joinPoint.proceed();
        } catch (Exception ex) {
            throw ex;
        } finally {
            // 메서드 실행 후 락 해제
            unLock(
                isLocked,
                key
            );
        }
    }

    private boolean lock(
        String key,
        String value,
        Duration lockTime
    ) {
        try {
            // Redis에 락 설정 시도
            return cacheStore.lock(
                key,
                value,
                lockTime
            );
        } catch (Exception e) {
            // 레디스 이슈로 인하여 오류 발생 시, 실제 메서드 실행을 위하여 true 리턴
            return true;
        }
    }

    private void unLock(
        boolean isLocked,
        String key
    ) {
        // 락이 설정되었다면, 해당 키를 Redis에서 제거하여 락 해제
        if (isLocked) {
            cacheStore.delete(key);
        }
    }
}
