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

    @Nested
    @DisplayName("주문(주문상품 포함) 조회")
    class GetOrderWithOrderProducts {
        @Test
        @DisplayName("주문(주문상품 포함) 조회 시 주문과 주문상품을 조회한다")
        void getOrderWithOrderProducts_ShouldReturnOrderWithProducts() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            OrderProduct op1 = createOrderProduct(1L, orderId, 1000, 1200, 200, 2);
            OrderProduct op2 = createOrderProduct(2L, orderId, 2000, 2500, 500, 1);
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