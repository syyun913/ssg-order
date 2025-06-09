package com.ssg.order.domain.common.util.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecutionLock {
    String keyPrefix() default "";

    String keyVariable(); // 변수명 ex) #loginUser.username

    String value() default "lock";

    long lockTimeMs() default 60000L; // ms
}
