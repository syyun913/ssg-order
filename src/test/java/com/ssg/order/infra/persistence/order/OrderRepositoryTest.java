package com.ssg.order.infra.persistence.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
import com.ssg.order.domain.domain.order.Order;
import com.ssg.order.domain.domain.order.OrderProduct;
import com.ssg.order.infra.persistence.order.entity.OrderEntity;
import com.ssg.order.infra.persistence.order.entity.OrderProductEntity;
import com.ssg.order.infra.persistence.order.mapper.OrderPersistenceMapper;
import com.ssg.order.infra.persistence.order.repository.OrderJpaRepository;
import com.ssg.order.infra.persistence.order.repository.OrderProductJpaRepository;
import java.util.ArrayList;
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
        orderProduct = OrderProduct.builder()
                .id(1L)
                .orderId(1L)
                .productId(100L)
                .quantity(2)
                .build();

        order = Order.builder()
                .id(1L)
                .userId(1L)
                .orderProducts(new ArrayList<>(List.of(orderProduct)))
                .build();

        orderEntity = OrderEntity.builder()
                .id(1L)
                .userId(1L)
                .build();

        orderProductEntity = OrderProductEntity.builder()
                .id(1L)
                .order(orderEntity)
                .productId(100L)
                .quantity(2)
                .build();
    }

    @DisplayName("주문 저장")
    @Nested
    class SaveOrderTests {

        @Test
        @DisplayName("주문 저장 성공 시 저장된 주문 정보가 리턴된다")
        void saveOrder_Success() {
            // given
            when(mapper.toEntity(any(Order.class))).thenReturn(orderEntity);
            when(orderJpaRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
            when(mapper.toEntity(any(OrderProduct.class), any(OrderEntity.class))).thenReturn(orderProductEntity);
            when(orderProductJpaRepository.saveAll(any())).thenReturn(List.of(orderProductEntity));
            when(mapper.toDomain(any(OrderEntity.class), any())).thenReturn(order);

            // when
            Order savedOrder = orderRepository.saveOrder(order);

            // then
            assertThat(savedOrder).isNotNull();
            assertThat(savedOrder.getId()).isEqualTo(1L);
            assertThat(savedOrder.getOrderProducts()).isNotNull();
            assertThat(savedOrder.getOrderProducts()).hasSize(1);
            
            OrderProduct savedOrderProduct = savedOrder.getOrderProducts().get(0);
            assertThat(savedOrderProduct.getId()).isEqualTo(1L);
            assertThat(savedOrderProduct.getProductId()).isEqualTo(100L);
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
            when(orderJpaRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(orderEntity));
            when(orderProductJpaRepository.findAllByOrder_Id(1L)).thenReturn(List.of(orderProductEntity));
            when(mapper.toDomain(any(OrderEntity.class), any())).thenReturn(order);

            // when
            Order foundOrder = orderRepository.getOrderWithOrderProductsById(1L, 1L);

            // then
            assertThat(foundOrder).isNotNull();
            assertThat(foundOrder.getId()).isEqualTo(1L);
            assertThat(foundOrder.getOrderProducts()).isNotNull();
            assertThat(foundOrder.getOrderProducts()).hasSize(1);
            
            OrderProduct foundOrderProduct = foundOrder.getOrderProducts().get(0);
            assertThat(foundOrderProduct.getId()).isEqualTo(1L);
            assertThat(foundOrderProduct.getProductId()).isEqualTo(100L);
            assertThat(foundOrderProduct.getQuantity()).isEqualTo(2);
        }

        @Test
        @DisplayName("주문 ID와 사용자 ID로 주문 조회 시 주문이 존재하지 않으면 예외가 리턴된다")
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
        @DisplayName("주문 ID와 사용자 ID로 주문 조회 시 주문상품이 존재하지 않으면 예외가 리턴된다")
        void getOrderWithOrderProductsById_NoOrderProducts_ThrowsException() {
            // given
            when(orderJpaRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(orderEntity));
            when(orderProductJpaRepository.findAllByOrder_Id(1L)).thenReturn(List.of());

            // when & then
            assertThatThrownBy(() -> orderRepository.getOrderWithOrderProductsById(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrorCode()).isEqualTo(BusinessErrorCode.NOT_FOUND_ORDER_PRODUCT);
                    assertThat(businessException.getMessage()).isEqualTo("주문 상품을 조회할 수 없습니다.");
                });
        }
    }
} 