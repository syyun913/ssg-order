package com.ssg.order.domain.domain.order.usecase;

import com.ssg.order.domain.common.annotation.UseCase;
import com.ssg.order.domain.domain.order.Order;
import com.ssg.order.domain.domain.order.OrderProduct;
import com.ssg.order.domain.domain.order.enumtype.OrderStatusCode;
import com.ssg.order.domain.domain.order.repository.OrderReadRepository;
import com.ssg.order.domain.domain.order.repository.OrderWriteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
class OrderUseCaseImpl implements OrderUseCase {
    private final OrderWriteRepository orderWriteRepository;
    private final OrderReadRepository orderReadRepository;

    @Override
    public Order createOrder(Long userId, List<OrderProduct> orderProducts) {
        int totalPaymentPrice = 0;
        int totalSellingPrice = 0;
        int totalDiscountAmount = 0;
        for(OrderProduct orderProduct : orderProducts) {
            totalPaymentPrice += orderProduct.getPaymentPrice();
            totalSellingPrice += orderProduct.getSellingPrice();
            totalDiscountAmount += orderProduct.getDiscountAmount();
        }

        Order order = Order.builder()
                .userId(userId)
                .statusCode(OrderStatusCode.ORDER_COMPLETED)
                .orderProducts(orderProducts)
                .paymentPrice(totalPaymentPrice)
                .sellingPrice(totalSellingPrice)
                .discountAmount(totalDiscountAmount)
                .build();

        Order savedOrder = orderWriteRepository.saveOrder(order);

        return savedOrder;
    }

    @Override
    public Order getOrderWithOrderProducts(Long orderId, Long userId) {
        return orderReadRepository.getOrderWithOrderProductsById(orderId, userId);
    }
} 