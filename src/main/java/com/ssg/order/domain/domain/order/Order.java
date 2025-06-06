package com.ssg.order.domain.domain.order;

import com.ssg.order.domain.domain.order.enumtype.OrderStatusCode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Order {
    private final Long id;
    private final Long userId;
    private final OrderStatusCode statusCode;
    private final Integer paymentPrice;
    private final Integer sellingPrice;
    private final Integer discountAmount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<OrderProduct> orderProducts;
} 