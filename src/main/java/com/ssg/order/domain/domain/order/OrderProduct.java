package com.ssg.order.domain.domain.order;

import com.ssg.order.domain.domain.order.enumtype.OrderProductStatusCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderProduct {
    private final Long id;
    private final Long orderId;
    private final Long productId;
    private final OrderProductStatusCode statusCode;
    private final Integer quantity;
    private final Integer paymentPrice;
    private final Integer sellingPrice;
    private final Integer discountAmount;
} 