package com.ssg.order.domain.common.annotation.exception.code;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum InternalErrorCode {
    REPLACE_SERVER_MESSAGE(
        INTERNAL_SERVER_ERROR,
        "{param}"
    ),

    /*
     * 409 CONFLICT
     */
    ALREADY_PROGRESS(
        CONFLICT,
        "이미 처리중인 요청입니다."
    )
    ;

    private final HttpStatus httpStatus;
    private String message;

    public String getName() {
        return name();
    }
}
