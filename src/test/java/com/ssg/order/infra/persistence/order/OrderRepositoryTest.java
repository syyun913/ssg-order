package com.ssg.order.infra.persistence.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
import com.ssg.order.domain.domain.order.Order;
import com.ssg.order.domain.domain.order.OrderProduct;
import com.ssg.order.domain.domain.order.enumtype.OrderProductStatusCode;
import com.ssg.order.domain.domain.order.enumtype.OrderStatusCode;
import com.ssg.order.infra.persistence.order.entity.OrderEntity;
import com.ssg.order.infra.persistence.order.entity.OrderProductEntity;
import com.ssg.order.infra.persistence.order.mapper.OrderPersistenceMapper;
import com.ssg.order.infra.persistence.order.repository.OrderJpaRepository;
import com.ssg.order.infra.persistence.order.repository.OrderProductJpaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryTest {

    @InjectMocks
    private OrderRepository orderRepository;

    @Mock
    private OrderPersistenceMapper mapper;

    @Mock
    private OrderJpaRepository orderJpaRepository;

    @Mock
    private OrderProductJpaRepository orderProductJpaRepository;

    private Order order;
    private OrderEntity orderEntity;
    private OrderProduct orderProduct;
    private OrderProductEntity orderProductEntity;

    @BeforeEach
    void setUp() {
        orderProduct = createOrderProduct(1L, 1L, 100L, 2);
        order = createOrder(1L, 1L);
        orderEntity = createOrderEntity(1L, 1L);
        orderProductEntity = createOrderProductEntity(1L, 1L, 100L, 2);
    }

    @DisplayName("주문 저장")
    @Nested
    class SaveOrderTests {
        @Test
        @DisplayName("주문 저장 성공 시 저장된 주문 정보가 리턴된다")
        void saveOrder_Success() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            Long productId = 100L;

            OrderProduct orderProduct = createOrderProduct(1L, orderId, productId, 2);
            Order order = Order.builder()
                .id(orderId)
                .userId(userId)
                .orderProducts(List.of(orderProduct))
                .build();

            OrderEntity orderEntity = createOrderEntity(orderId, userId);
            OrderProductEntity orderProductEntity = createOrderProductEntity(1L, orderId, productId, 2);

            when(mapper.toEntity(any(Order.class))).thenReturn(orderEntity);
            when(orderJpaRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
            when(mapper.toEntity(any(OrderProduct.class), any(OrderEntity.class))).thenReturn(orderProductEntity);
            when(orderProductJpaRepository.saveAll(any())).thenReturn(List.of(orderProductEntity));
            when(mapper.toDomain(any(OrderEntity.class), any())).thenReturn(order);

            // when
            Order savedOrder = orderRepository.saveOrder(order);

            // then
            assertThat(savedOrder).isNotNull();
            assertThat(savedOrder.getId()).isEqualTo(orderId);
            assertThat(savedOrder.getOrderProducts()).isNotNull();
            assertThat(savedOrder.getOrderProducts()).hasSize(1);
            
            OrderProduct savedOrderProduct = savedOrder.getOrderProducts().get(0);
            assertThat(savedOrderProduct.getId()).isEqualTo(1L);
            assertThat(savedOrderProduct.getProductId()).isEqualTo(productId);
            assertThat(savedOrderProduct.getQuantity()).isEqualTo(2);
        }
    }
    
    @DisplayName("주문 ID와 사용자 ID로 주문 조회")
    @Nested
    class GetOrderWithOrderProductsByIdTests {

        @Test
        @DisplayName("주문 ID와 사용자 ID로 주문 조회 성공 시 주문과 주문상품 정보가 리턴된다")
        void getOrderWithOrderProductsById_Success() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            OrderEntity orderEntity = createOrderEntity(orderId, userId);
            OrderProductEntity orderProductEntity = createOrderProductEntity(1L, orderId, 100L, 2);
            List<OrderProductEntity> orderProductEntities = List.of(orderProductEntity);

            OrderProduct orderProduct = createOrderProduct(1L, orderId, 100L, 2);
            Order expectedOrder = Order.builder()
                .id(orderId)
                .userId(userId)
                .orderProducts(List.of(orderProduct))
                .build();

            when(orderJpaRepository.findByIdAndUserId(orderId, userId)).thenReturn(Optional.of(orderEntity));
            when(orderProductJpaRepository.findAllByOrderId(orderId)).thenReturn(orderProductEntities);
            when(mapper.toDomain(orderEntity, orderProductEntities)).thenReturn(expectedOrder);

            // when
            Order foundOrder = orderRepository.getOrderWithOrderProductsById(orderId, userId);

            // then
            assertThat(foundOrder).isNotNull();
            assertThat(foundOrder.getId()).isEqualTo(orderId);
            assertThat(foundOrder.getOrderProducts()).isNotNull();
            assertThat(foundOrder.getOrderProducts()).hasSize(1);
            
            OrderProduct foundOrderProduct = foundOrder.getOrderProducts().get(0);
            assertThat(foundOrderProduct.getId()).isEqualTo(1L);
            assertThat(foundOrderProduct.getProductId()).isEqualTo(100L);
            assertThat(foundOrderProduct.getQuantity()).isEqualTo(2);
        }

        @Test
        @DisplayName("주문 ID와 사용자 ID로 주문 조회 시 주문이 존재하지 않으면 BusinessException이 리턴된다")
        void getOrderWithOrderProductsById_NotFoundOrder_ThrowsException() {
            // given
            when(orderJpaRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderRepository.getOrderWithOrderProductsById(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode()).isEqualTo(BusinessErrorCode.NOT_FOUND_ORDER);
                    assertThat(businessException.getMessage()).isEqualTo("주문 정보를 조회할 수 없습니다.");
                });
        }

        @Test
        @DisplayName("주문 ID와 사용자 ID로 주문 조회 시 주문상품이 존재하지 않으면 BusinessException이 리턴된다")
        void getOrderWithOrderProductsById_NoOrderProducts_ThrowsException() {
            // given
            when(orderJpaRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(orderEntity));
            when(orderProductJpaRepository.findAllByOrderId(1L)).thenReturn(List.of());

            // when & then
            assertThatThrownBy(() -> orderRepository.getOrderWithOrderProductsById(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode()).isEqualTo(BusinessErrorCode.NOT_FOUND_ORDER_PRODUCT);
                    assertThat(businessException.getMessage()).isEqualTo("주문 상품을 찾을 수 없습니다.");
                });
        }
    }

    @DisplayName("사용자 ID로 주문 목록 조회")
    @Nested
    class FindOrdersByUserIdTests {
        @Test
        @DisplayName("사용자 ID로 주문 목록 조회 성공 시 주문 목록이 리턴된다")
        void findOrdersByUserId_Success() {
            // given
            Long userId = 1L;
            OrderEntity orderEntity1 = createOrderEntity(1L, userId);
            OrderEntity orderEntity2 = createOrderEntity(2L, userId);
            List<OrderEntity> orderEntities = List.of(orderEntity1, orderEntity2);

            Order order1 = createOrder(1L, userId);
            Order order2 = createOrder(2L, userId);
            List<Order> expectedOrders = List.of(order1, order2);

            when(orderJpaRepository.findAllByUserIdOrderByIdDesc(userId)).thenReturn(orderEntities);
            when(mapper.toDomain(orderEntity1, null)).thenReturn(order1);
            when(mapper.toDomain(orderEntity2, null)).thenReturn(order2);

            // when
            List<Order> result = orderRepository.findOrdersByUserId(userId, true);

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
            when(orderJpaRepository.findAllByUserIdOrderByIdDesc(userId)).thenReturn(List.of());

            // when
            List<Order> result = orderRepository.findOrdersByUserId(userId, true);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("사용자 ID로 주문 목록 조회 시 내림차순으로 정렬된 결과가 리턴된다")
        void findOrdersByUserId_WithDescendingOrder_ShouldReturnSortedList() {
            // given
            Long userId = 1L;
            OrderEntity orderEntity1 = createOrderEntity(1L, userId);
            OrderEntity orderEntity2 = createOrderEntity(2L, userId);
            List<OrderEntity> orderEntities = List.of(orderEntity2, orderEntity1);

            Order order1 = createOrder(1L, userId);
            Order order2 = createOrder(2L, userId);
            List<Order> expectedOrders = List.of(order2, order1);

            when(orderJpaRepository.findAllByUserIdOrderByIdDesc(userId)).thenReturn(orderEntities);
            when(mapper.toDomain(orderEntity2, null)).thenReturn(order2);
            when(mapper.toDomain(orderEntity1, null)).thenReturn(order1);

            // when
            List<Order> result = orderRepository.findOrdersByUserId(userId, true);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expectedOrders);
            assertThat(result.get(0).getId()).isGreaterThan(result.get(1).getId());
        }
    }

    @DisplayName("주문 상품 취소")
    @Nested
    class CancelOrderProductTests {
        @Test
        @DisplayName("주문 상품 취소 성공 시 취소된 주문 상품 정보가 리턴된다")
        void cancelOrderProduct_Success() {
            // given
            Long orderId = 1L;
            Long orderProductId = 1L;
            Long userId = 1L;

            OrderEntity orderEntity = createOrderEntity(orderId, userId);
            OrderProductEntity orderProductEntity = createOrderProductEntity(orderProductId, orderId, 100L, 2);

            OrderProduct expectedOrderProduct = OrderProduct.builder()
                .id(orderProductId)
                .orderId(orderId)
                .productId(100L)
                .statusCode(OrderProductStatusCode.CANCELED)
                .quantity(2)
                .build();

            when(orderJpaRepository.findByIdAndUserId(orderId, userId)).thenReturn(Optional.of(orderEntity));
            when(orderProductJpaRepository.findByIdAndOrderId(orderProductId, orderId)).thenReturn(Optional.of(orderProductEntity));
            when(orderProductJpaRepository.existsByOrderIdAndStatusCodeNot(orderId, OrderProductStatusCode.CANCELED)).thenReturn(true);
            when(orderProductJpaRepository.save(any(OrderProductEntity.class))).thenReturn(orderProductEntity);
            when(mapper.toDomain(any(OrderProductEntity.class))).thenReturn(expectedOrderProduct);

            // when
            OrderProduct result = orderRepository.cancelOrderProduct(orderId, orderProductId, userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(orderProductId);
            assertThat(result.getOrderId()).isEqualTo(orderId);
            assertThat(result.getStatusCode()).isEqualTo(OrderProductStatusCode.CANCELED);
        }

        @Test
        @DisplayName("주문 상품 취소 요청 시 이미 취소된 주문 상품을 취소하려고 하면 BusinessException이 리턴된다")
        void cancelOrderProduct_AlreadyCanceled_ThrowsException() {
            // given
            Long orderId = 1L;
            Long orderProductId = 1L;
            Long userId = 1L;

            OrderEntity orderEntity = createOrderEntity(orderId, userId);
            OrderProductEntity orderProductEntity = OrderProductEntity.builder()
                .id(orderProductId)
                .order(orderEntity)
                .productId(100L)
                .statusCode(OrderProductStatusCode.CANCELED)
                .quantity(2)
                .build();

            when(orderJpaRepository.findByIdAndUserId(orderId, userId)).thenReturn(Optional.of(orderEntity));
            when(orderProductJpaRepository.findByIdAndOrderId(orderProductId, orderId)).thenReturn(Optional.of(orderProductEntity));

            // when & then
            assertThatThrownBy(() -> orderRepository.cancelOrderProduct(orderId, orderProductId, userId))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode()).isEqualTo(BusinessErrorCode.ALREADY_CANCELED_ORDER_PRODUCT);
                });
        }

        @Test
        @DisplayName("주문 상품 취소 요청 시 모든 주문 상품이 취소되면 주문 상태도 취소로 변경된다")
        void cancelOrderProduct_AllProductsCanceled_ChangesOrderStatus() {
            // given
            Long orderId = 1L;
            Long orderProductId = 1L;
            Long userId = 1L;

            OrderEntity orderEntity = createOrderEntity(orderId, userId);
            OrderProductEntity orderProductEntity = createOrderProductEntity(orderProductId, orderId, 100L, 2);

            OrderProduct expectedOrderProduct = OrderProduct.builder()
                .id(orderProductId)
                .orderId(orderId)
                .productId(100L)
                .statusCode(OrderProductStatusCode.CANCELED)
                .quantity(2)
                .build();

            when(orderJpaRepository.findByIdAndUserId(orderId, userId)).thenReturn(Optional.of(orderEntity));
            when(orderProductJpaRepository.findByIdAndOrderId(orderProductId, orderId)).thenReturn(Optional.of(orderProductEntity));
            when(orderProductJpaRepository.existsByOrderIdAndStatusCodeNot(orderId, OrderProductStatusCode.CANCELED)).thenReturn(false);
            when(orderProductJpaRepository.save(any(OrderProductEntity.class))).thenReturn(orderProductEntity);
            when(mapper.toDomain(any(OrderProductEntity.class))).thenReturn(expectedOrderProduct);

            // when
            OrderProduct result = orderRepository.cancelOrderProduct(orderId, orderProductId, userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatusCode()).isEqualTo(OrderProductStatusCode.CANCELED);
            assertThat(orderEntity.getStatusCode()).isEqualTo(OrderStatusCode.CANCELED);
        }
    }

    @DisplayName("주문 금액 차감")
    @Nested
    class SubtractOrderPriceTests {
        @Test
        @DisplayName("주문 금액 차감 성공 시 차감된 주문 정보가 리턴된다")
        void subtractOrderPrice_Success() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            Integer subtractPaymentPrice = 1000;
            Integer subtractSellingPrice = 1200;
            Integer subtractDiscountAmount = 200;

            OrderEntity orderEntity = OrderEntity.builder()
                .id(orderId)
                .userId(userId)
                .paymentPrice(3000)
                .sellingPrice(3700)
                .discountAmount(700)
                .build();

            Order expectedOrder = Order.builder()
                .id(orderId)
                .userId(userId)
                .paymentPrice(2000)
                .sellingPrice(2500)
                .discountAmount(500)
                .build();

            when(orderJpaRepository.findByIdAndUserId(orderId, userId)).thenReturn(Optional.of(orderEntity));
            when(orderJpaRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
            when(mapper.toDomain(any(OrderEntity.class), any())).thenReturn(expectedOrder);

            // when
            Order result = orderRepository.subtractOrderPrice(
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
        @DisplayName("주문 금액 차감 시 차감 금액이 기존 금액보다 크면 BusinessException이 리턴된다")
        void subtractOrderPrice_ExceedsOriginalAmount_ThrowsException() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            Integer subtractPaymentPrice = 4000; // 기존 금액보다 큰 금액
            Integer subtractSellingPrice = 1200;
            Integer subtractDiscountAmount = 200;

            OrderEntity orderEntity = OrderEntity.builder()
                .id(orderId)
                .userId(userId)
                .paymentPrice(3000)
                .sellingPrice(3700)
                .discountAmount(700)
                .build();

            when(orderJpaRepository.findByIdAndUserId(orderId, userId)).thenReturn(Optional.of(orderEntity));

            // when & then
            assertThatThrownBy(() -> orderRepository.subtractOrderPrice(
                orderId,
                userId,
                subtractPaymentPrice,
                subtractSellingPrice,
                subtractDiscountAmount
            )).isInstanceOf(BusinessException.class)
              .satisfies(exception -> {
                  BusinessException businessException = (BusinessException) exception;
                  assertThat(businessException.getErrorCode()).isEqualTo(BusinessErrorCode.ORDER_PRICE_CANNOT_BE_NEGATIVE);
              });
        }
    }

    private OrderEntity createOrderEntity(Long id, Long userId) {
        return OrderEntity.builder()
                .id(id)
                .userId(userId)
                .build();
    }

    private Order createOrder(Long id, Long userId) {
        return Order.builder()
                .id(id)
                .userId(userId)
                .orderProducts(List.of())
                .build();
    }

    private OrderProductEntity createOrderProductEntity(Long id, Long orderId, Long productId, int quantity) {
        return OrderProductEntity.builder()
                .id(id)
                .order(OrderEntity.builder().id(orderId).build())
                .productId(productId)
                .quantity(quantity)
                .statusCode(OrderProductStatusCode.ORDER_COMPLETED)
                .build();
    }

    private OrderProduct createOrderProduct(Long id, Long orderId, Long productId, int quantity) {
        return OrderProduct.builder()
                            .id(id)
                            .orderId(orderId)
                            .productId(productId)
                            .quantity(quantity)
                            .statusCode(OrderProductStatusCode.ORDER_COMPLETED)
                            .build();
    }
} 