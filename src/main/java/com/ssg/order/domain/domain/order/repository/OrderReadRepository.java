package com.ssg.order.domain.domain.order.repository;

import com.ssg.order.domain.domain.order.Order;
import java.util.List;

public interface OrderReadRepository {
    Order getOrderWithOrderProductsById(Long orderId, Long userId);
    List<Order> findOrdersByUserId(Long userId, boolean isDescending);
}
