package com.ssg.order.domain.domain.order.repository;

import com.ssg.order.domain.domain.order.Order;

public interface OrderReadRepository {
    Order getOrderWithOrderProductsById(Long orderId, Long userId);
}
