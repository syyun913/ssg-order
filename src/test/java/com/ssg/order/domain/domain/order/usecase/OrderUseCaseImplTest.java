package com.ssg.order.domain.domain.order.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.ssg.order.domain.domain.order.Order;
import com.ssg.order.domain.domain.order.OrderProduct;
import com.ssg.order.domain.domain.order.enumtype.OrderProductStatusCode;
import com.ssg.order.domain.domain.order.enumtype.OrderStatusCode;
import com.ssg.order.domain.domain.order.repository.OrderReadRepository;
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

    @Mock
    private OrderReadRepository orderReadRepository;

    @Nested
    @DisplayName("주문 생성")
    class CreateOrder {
        @Test
        @DisplayName("주문 생성 요청 시 주문 상품의 합산된 금액이 계산되어 저장된다")
        void createOrder_ShouldCalculateTotalsAndSaveOrder() {
            // given
            Long userId = 1L;
            OrderProduct op1 = createOrderProduct(1L, 1L, OrderProductStatusCode.ORDER_COMPLETED, 1000, 1200, 200, 2);
            OrderProduct op2 = createOrderProduct(2L, 1L, OrderProductStatusCode.ORDER_COMPLETED, 2000, 2500, 500, 1);
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
            OrderProduct op = createOrderProduct(3L, 2L, OrderProductStatusCode.ORDER_COMPLETED, 500, 600, 100, 1);
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

    @Nested
    @DisplayName("주문(주문상품 포함) 조회")
    class GetOrderWithOrderProducts {
        @Test
        @DisplayName("주문(주문상품 포함) 조회 시 주문과 주문상품을 조회한다")
        void getOrderWithOrderProducts_ShouldReturnOrderWithProducts() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            OrderProduct op1 = createOrderProduct(1L, orderId, OrderProductStatusCode.ORDER_COMPLETED, 1000, 1200, 200, 2);
            OrderProduct op2 = createOrderProduct(2L, orderId, OrderProductStatusCode.ORDER_COMPLETED, 2000, 2500, 500, 1);
            List<OrderProduct> orderProducts = List.of(op1, op2);
            Order expectedOrder = createOrder(orderId, userId, orderProducts, 3000, 3700, 700);
            
            when(orderReadRepository.getOrderWithOrderProductsById(orderId, userId))
                .thenReturn(expectedOrder);

            // when
            Order result = orderUseCase.getOrderWithOrderProducts(orderId, userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(orderId);
            assertThat(result.getUserId()).isEqualTo(userId);
            assertThat(result.getOrderProducts()).hasSize(2);
            assertThat(result.getOrderProducts()).containsExactlyInAnyOrder(op1, op2);
            assertThat(result.getPaymentPrice()).isEqualTo(3000);
            assertThat(result.getSellingPrice()).isEqualTo(3700);
            assertThat(result.getDiscountAmount()).isEqualTo(700);
        }
    }

    @Nested
    @DisplayName("사용자 ID로 주문 목록 조회")
    class FindOrdersByUserId {
        @Test
        @DisplayName("사용자 ID로 주문 목록을 조회 시 주문 목록이 리턴된다")
        void findOrdersByUserId_ShouldReturnOrderList() {
            // given
            Long userId = 1L;
            Order order1 = createOrder(1L, userId, List.of(), 1000, 1200, 200);
            Order order2 = createOrder(2L, userId, List.of(), 2000, 2500, 500);
            List<Order> expectedOrders = List.of(order1, order2);

            when(orderReadRepository.findOrdersByUserId(userId, true)).thenReturn(expectedOrders);

            // when
            List<Order> result = orderUseCase.findOrdersByUserId(userId, true);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expectedOrders);
        }

        @Test
        @DisplayName("사용자 ID로 주문 목록 조회 시 주문이 없으면 빈 배열이 리턴된다")
        void findOrdersByUserId_WithNoOrders_ShouldReturnEmptyList() {
            // given
            Long userId = 1L;
            when(orderReadRepository.findOrdersByUserId(userId, true)).thenReturn(List.of());

            // when
            List<Order> result = orderUseCase.findOrdersByUserId(userId, true);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("사용자 ID로 주문 목록 조회 시 내림차순을 true로 넘기면 내림차순으로 정렬된 결과가 리턴된다")
        void findOrdersByUserId_WithAscendingOrder_ShouldReturnSortedList() {
            // given
            Long userId = 1L;
            Order order1 = createOrder(1L, userId, List.of(), 1000, 1200, 200);
            Order order2 = createOrder(2L, userId, List.of(), 2000, 2500, 500);
            List<Order> expectedOrders = List.of(order2, order1);

            when(orderReadRepository.findOrdersByUserId(userId, true)).thenReturn(expectedOrders);

            // when
            List<Order> result = orderUseCase.findOrdersByUserId(userId, true);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expectedOrders);
            assertThat(result.get(0).getId()).isGreaterThan(result.get(1).getId());
        }
    }

    @Nested
    @DisplayName("주문 상품 취소")
    class CancelOrderProduct {
        @Test
        @DisplayName("주문 상품 취소 시 주문 상품의 상태가 취소로 변경된다")
        void cancelOrderProduct_ShouldChangeStatusToCanceled() {
            // given
            Long orderId = 1L;
            Long orderProductId = 1L;
            Long userId = 1L;

            OrderProduct orderProduct = createOrderProduct(orderProductId, orderId, OrderProductStatusCode.CANCELED, 1000, 1200, 200, 2);

            when(orderWriteRepository.cancelOrderProduct(orderId, orderProductId, userId))
                .thenReturn(orderProduct);

            // when
            OrderProduct result = orderUseCase.cancelOrderProduct(orderId, orderProductId, userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(orderProductId);
            assertThat(result.getOrderId()).isEqualTo(orderId);
            assertThat(result.getStatusCode()).isEqualTo(OrderProductStatusCode.CANCELED);
        }
    }

    @Nested
    @DisplayName("주문 금액 차감")
    class SubstractOrderPrice {
        @Test
        @DisplayName("주문 금액 차감 시 주문의 금액이 정상적으로 차감된다")
        void substractOrderPrice_ShouldSubtractAmountsCorrectly() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            Integer subtractPaymentPrice = 1000;
            Integer subtractSellingPrice = 1200;
            Integer subtractDiscountAmount = 200;

            // 기존 주문 생성
            Order originalOrder = createOrder(orderId, userId, List.of(), 3000, 3700, 700);
            
            // 차감 후 예상되는 주문
            Order expectedOrder = createOrder(orderId, userId, List.of(), 2000, 2500, 500);

            when(orderWriteRepository.substractOrderPrice(
                orderId,
                userId,
                subtractPaymentPrice,
                subtractSellingPrice,
                subtractDiscountAmount
            )).thenReturn(expectedOrder);

            // when
            Order result = orderUseCase.substractOrderPrice(
                orderId,
                userId,
                subtractPaymentPrice,
                subtractSellingPrice,
                subtractDiscountAmount
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(orderId);
            assertThat(result.getUserId()).isEqualTo(userId);
            assertThat(result.getPaymentPrice()).isEqualTo(2000); // 3000 - 1000
            assertThat(result.getSellingPrice()).isEqualTo(2500); // 3700 - 1200
            assertThat(result.getDiscountAmount()).isEqualTo(500); // 700 - 200
        }

        @Test
        @DisplayName("주문 금액 차감 시 모든 금액이 0이 되면 주문의 금액이 0이 된다")
        void substractOrderPrice_WithFullAmount_ShouldResultInZeroAmounts() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            Integer subtractPaymentPrice = 3000;
            Integer subtractSellingPrice = 3700;
            Integer subtractDiscountAmount = 700;

            // 기존 주문 생성
            Order originalOrder = createOrder(orderId, userId, List.of(), 3000, 3700, 700);
            
            // 차감 후 예상되는 주문
            Order expectedOrder = createOrder(orderId, userId, List.of(), 0, 0, 0);

            when(orderWriteRepository.substractOrderPrice(
                orderId,
                userId,
                subtractPaymentPrice,
                subtractSellingPrice,
                subtractDiscountAmount
            )).thenReturn(expectedOrder);

            // when
            Order result = orderUseCase.substractOrderPrice(
                orderId,
                userId,
                subtractPaymentPrice,
                subtractSellingPrice,
                subtractDiscountAmount
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(orderId);
            assertThat(result.getUserId()).isEqualTo(userId);
            assertThat(result.getPaymentPrice()).isZero();
            assertThat(result.getSellingPrice()).isZero();
            assertThat(result.getDiscountAmount()).isZero();
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

    private OrderProduct createOrderProduct(Long id, Long orderId, OrderProductStatusCode statusCode, int paymentPrice, int sellingPrice, int discountAmount, int quantity) {
        return OrderProduct.builder()
                           .id(id)
                           .orderId(orderId)
                           .productId(id)
                           .statusCode(statusCode)
                           .quantity(quantity)
                           .paymentPrice(paymentPrice)
                           .sellingPrice(sellingPrice)
                           .discountAmount(discountAmount)
                           .build();
    }
}