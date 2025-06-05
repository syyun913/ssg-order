package com.ssg.order.domain.domain.order.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.ssg.order.domain.domain.order.Order;
import com.ssg.order.domain.domain.order.OrderProduct;
import com.ssg.order.domain.domain.order.enumtype.OrderProductStatusCode;
import com.ssg.order.domain.domain.order.enumtype.OrderStatusCode;
import com.ssg.order.domain.domain.order.repository.OrderWriteRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("주문 유즈케이스 테스트")
@ExtendWith(MockitoExtension.class)
class OrderUseCaseImplTest {
    @InjectMocks
    private OrderUseCaseImpl orderUseCase;

    @Mock
    private OrderWriteRepository orderWriteRepository;

    @Nested
    @DisplayName("주문 생성")
    class CreateOrder {
        @Test
        @DisplayName("주문 생성 요청 시 주문 상품의 합산된 금액이 계산되어 저장된다")
        void createOrder_ShouldCalculateTotalsAndSaveOrder() {
            // given
            Long userId = 1L;
            OrderProduct op1 = createOrderProduct(1L, 1L, 1000, 1200, 200, 2);
            OrderProduct op2 = createOrderProduct(2L, 1L, 2000, 2500, 500, 1);
            List<OrderProduct> orderProducts = List.of(op1, op2);
            Order expectedOrder = createOrder(null, userId, orderProducts, 1000 + 2000, 1200 + 2500, 200 + 500);
            when(orderWriteRepository.saveOrder(org.mockito.Mockito.any(Order.class))).thenReturn(expectedOrder);

            // when
            Order result = orderUseCase.createOrder(userId, orderProducts);

            // then
            assertThat(result.getUserId()).isEqualTo(userId);
            assertThat(result.getOrderProducts()).isEqualTo(orderProducts);
            assertThat(result.getPaymentPrice()).isEqualTo(3000);
            assertThat(result.getSellingPrice()).isEqualTo(3700);
            assertThat(result.getDiscountAmount()).isEqualTo(700);
            assertThat(result.getStatusCode()).isEqualTo(OrderStatusCode.ORDER_COMPLETED);
        }

        @Test
        @DisplayName("단일 상품으로 주문 생성 요청 시 해당 상품의 값이 그대로 반영된다")
        void createOrder_WithSingleProduct_ShouldReturnOrderWithProductValues() {
            // given
            Long userId = 3L;
            OrderProduct op = createOrderProduct(3L, 2L, 500, 600, 100, 1);
            List<OrderProduct> orderProducts = List.of(op);
            Order expectedOrder = createOrder(null, userId, orderProducts, 500, 600, 100);
            when(orderWriteRepository.saveOrder(org.mockito.Mockito.any(Order.class))).thenReturn(expectedOrder);

            // when
            Order result = orderUseCase.createOrder(userId, orderProducts);

            // then
            assertThat(result.getUserId()).isEqualTo(userId);
            assertThat(result.getOrderProducts()).containsExactly(op);
            assertThat(result.getPaymentPrice()).isEqualTo(500);
            assertThat(result.getSellingPrice()).isEqualTo(600);
            assertThat(result.getDiscountAmount()).isEqualTo(100);
        }
    }

    private Order createOrder(Long id, Long userId, List<OrderProduct> orderProducts, int paymentPrice, int sellingPrice, int discountAmount) {
        return Order.builder()
                    .id(id)
                    .userId(userId)
                    .statusCode(OrderStatusCode.ORDER_COMPLETED)
                    .orderProducts(orderProducts)
                    .paymentPrice(paymentPrice)
                    .sellingPrice(sellingPrice)
                    .discountAmount(discountAmount)
                    .build();
    }

    private OrderProduct createOrderProduct(Long id, Long orderId, int paymentPrice, int sellingPrice, int discountAmount, int quantity) {
        return OrderProduct.builder()
                           .id(id)
                           .orderId(orderId)
                           .productId(id)
                           .statusCode(OrderProductStatusCode.ORDER_COMPLETED)
                           .quantity(quantity)
                           .paymentPrice(paymentPrice)
                           .sellingPrice(sellingPrice)
                           .discountAmount(discountAmount)
                           .build();
    }
}