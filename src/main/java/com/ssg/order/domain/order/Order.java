package com.ssg.order.domain.order;

import com.ssg.order.domain.order.enumtype.OrderStatusCode;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Order {
    private final Long id;
    private final Long userId;
    private final OrderStatusCode statusCode;
    private final Integer paymentPrice;
    private final Integer sellingPrice;
    private final Integer discountAmount;
    private final List<OrderProduct> orderProducts;
} 