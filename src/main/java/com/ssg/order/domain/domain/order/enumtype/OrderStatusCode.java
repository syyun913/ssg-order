package com.ssg.order.domain.domain.order.enumtype;

import lombok.Getter;

@Getter
public enum OrderStatusCode {
    ORDER_COMPLETED("주문 완료"),
    CANCELED("취소")
    ;

    private final String description;

    OrderStatusCode(String description) {
        this.description = description;
    }
}
