package com.ssg.order.domain.order.usecase;

import com.ssg.order.domain.order.Order;
import com.ssg.order.domain.order.OrderProduct;
import java.util.List;

public interface OrderUseCase {
    Order createOrder(Long userId, List<OrderProduct> orderProducts);
} 