package com.ssg.order.domain.domain.order.usecase;

import com.ssg.order.domain.domain.order.Order;
import com.ssg.order.domain.domain.order.OrderProduct;
import java.util.List;

public interface OrderUseCase {
    Order createOrder(Long userId, List<OrderProduct> orderProducts);
    Order getOrderWithOrderProducts(Long orderId, Long userId);
    List<Order> findOrdersByUserId(Long userId, boolean isDescending);
} 