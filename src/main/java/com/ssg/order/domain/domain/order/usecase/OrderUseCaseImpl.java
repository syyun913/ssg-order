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

    /**
     * 주문을 생성합니다.
     * @param userId 사용자 ID
     * @param orderProducts 주문에 포함될 상품 목록
     * @return 생성된 주문 정보
     */
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

    /**
     * 주문 상품과 주문 정보를 조회합니다.
     * @param orderId 주문 ID
     * @param userId 사용자 ID
     * @return 주문 상품 목록과 주문 정보
     */
    @Override
    public Order getOrderWithOrderProducts(Long orderId, Long userId) {
        return orderReadRepository.getOrderWithOrderProductsById(orderId, userId);
    }

    /**
     * 사용자 ID로 주문 목록을 조회합니다.
     * @param userId 사용자 ID
     * @param isDescending 내림차순 여부
     * @return 주문 목록
     */
    @Override
    public List<Order> findOrdersByUserId(Long userId, boolean isDescending) {
        return orderReadRepository.findOrdersByUserId(userId, isDescending);
    }

    /**
     * 주문 상품을 취소하고, 주문 금액을 차감합니다.
     * @param orderId 주문 ID
     * @param orderProductId 주문 상품 ID
     * @param userId 사용자 ID
     * @return 취소된 주문 상품 정보
     */
    @Override
    public OrderProduct cancelOrderProduct(Long orderId, Long orderProductId, Long userId) {
        return orderWriteRepository.cancelOrderProduct(orderId, orderProductId, userId);
    }

    /**
     * 주문 금액을 차감합니다.
     * @param orderId 주문 ID
     * @param userId 사용자 ID
     * @param subtractPaymentPrice 차감할 결제 금액
     * @param subtractSellingPrice 차감할 판매 금액
     * @param subtractDiscountAmount 차감할 할인 금액
     * @return 차감된 주문 정보
     */
    @Override
    public Order subtractOrderPrice(Long orderId,
                                  Long userId,
                                  Integer subtractPaymentPrice,
                                  Integer subtractSellingPrice,
                                  Integer subtractDiscountAmount) {
        return orderWriteRepository.subtractOrderPrice(orderId, userId, subtractPaymentPrice, subtractSellingPrice, subtractDiscountAmount);
    }
} 