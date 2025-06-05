package com.ssg.order.domain.domain.order.repository;

import com.ssg.order.domain.domain.order.Order;

public interface OrderWriteRepository {
    Order saveOrder(Order order);
} 