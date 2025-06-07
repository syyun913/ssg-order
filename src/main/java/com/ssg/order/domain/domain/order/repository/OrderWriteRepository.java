package com.ssg.order.domain.domain.order.repository;

import com.ssg.order.domain.domain.order.Order;
import com.ssg.order.domain.domain.order.OrderProduct;

public interface OrderWriteRepository {
    Order saveOrder(Order order);
    OrderProduct cancelOrderProduct(Long orderId, Long orderProductId, Long userId);
    Order substractOrderPrice(Long orderId, Long userId, Integer subtractPaymentPrice, Integer subtractSellingPrice, Integer subtractDiscountAmount);
} 