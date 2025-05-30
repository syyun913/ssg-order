package com.ssg.order.domain.order.enumtype;

import com.ssg.order.domain.common.enumtype.EnumType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderProductStatusCode implements EnumType {
    ORDER_COMPLETED("1", "주문 완료"),
    CANCEL("10", "취소")
    ;

    private final String value;
    private final String description;

    public static OrderProductStatusCode ofValue(String value) {
        return EnumType.ofValue(OrderProductStatusCode.class, value);
    }

}
