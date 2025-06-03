package com.ssg.order.domain.common.annotation.exception.code;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BusinessErrorCode {
    NOT_FOUND_USER(
        BAD_REQUEST,
        "사용자를 찾을 수 없습니다."
    ),
    EXIST_USER(
        BAD_REQUEST,
        "이미 존재하는 사용자입니다."
    ),
    INVALID_TOKEN(
        BAD_REQUEST,
        "유효하지 않은 토큰입니다."
    )
    ;

    private final HttpStatus httpStatus;
    private String message;

    public String getName() {
        return name();
    }
}
