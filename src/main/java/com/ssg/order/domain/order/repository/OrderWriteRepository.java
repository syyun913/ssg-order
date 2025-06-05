package com.ssg.order.domain.order.repository;

import com.ssg.order.domain.order.Order;

public interface OrderWriteRepository {
    Order saveOrder(Order order);
} 