package com.ssg.order.domain.domain.order.enumtype;

import lombok.Getter;

@Getter
public enum OrderProductStatusCode {
    ORDER_COMPLETED("주문 완료"),
    CANCEL("취소")
    ;

    private final String description;

    OrderProductStatusCode(String description) {
        this.description = description;
    }

}
