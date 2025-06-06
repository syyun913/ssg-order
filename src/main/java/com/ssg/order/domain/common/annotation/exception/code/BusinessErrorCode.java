package com.ssg.order.domain.common.annotation.exception.code;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BusinessErrorCode {
    /*
     * 400 BAD_REQUEST
     */
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
    ),
    OUT_OF_STOCK(
        BAD_REQUEST,
        "상품 재고가 부족합니다."
    ),
    NOT_FOUND_PRODUCT(
        BAD_REQUEST,
        "상품을 조회할 수 없습니다."
    ),
    NOT_FOUND_ORDER(
        BAD_REQUEST,
        "주문 정보를 조회할 수 없습니다."
    ),
    NOT_FOUND_ORDER_PRODUCT(
        BAD_REQUEST,
        "주문 상품을 조회할 수 없습니다."
    )
    ;

    private final HttpStatus httpStatus;
    private String message;

    public String getName() {
        return name();
    }
}
