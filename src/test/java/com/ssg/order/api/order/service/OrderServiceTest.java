package com.ssg.order.api.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ssg.order.api.order.mapper.OrderDtoMapper;
import com.ssg.order.api.order.service.request.CreateOrderProductRequest;
import com.ssg.order.api.order.service.request.CreateOrderRequest;
import com.ssg.order.api.order.service.response.OrderCreateResponse;
import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
import com.ssg.order.domain.domain.order.Order;
import com.ssg.order.domain.domain.order.OrderProduct;
import com.ssg.order.domain.domain.order.usecase.OrderUseCase;
import com.ssg.order.domain.domain.product.Product;
import com.ssg.order.domain.domain.product.usecase.ProductUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("주문 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderUseCase orderUseCase;

    @Mock
    private ProductUseCase productUseCase;

    @Mock
    private OrderDtoMapper orderDtoMapper;

    @Nested
    @DisplayName("주문 생성")
    class CreateOrder {
        @Test
        @DisplayName("주문 생성 요청 시 상품이 존재하고 재고가 충분할 경우 주문이 성공적으로 생성된다")
        void createOrder_ShouldSucceed() {
            // given
            Long userId = 1L;
            CreateOrderProductRequest orderProductRequest = createOrderProductRequest(1L, 2);
            CreateOrderRequest request = createOrderRequest(List.of(orderProductRequest));

            Product product = createProduct(1L, "Test Product", 1000, 100, 10);

            OrderProduct orderProduct = createOrderProduct(1L, 2, 2000, 200, 1800);

            Order order = createOrder(1L, userId, List.of(orderProduct));

            OrderCreateResponse expectedResponse = OrderCreateResponse.builder()
                                                                      .orderId(1L)
                                                                      .build();

            when(productUseCase.findProductsByProductIds(List.of(1L))).thenReturn(List.of(product));
            when(orderDtoMapper.toOrderProduct(any())).thenReturn(orderProduct);
            when(orderUseCase.createOrder(userId, List.of(orderProduct))).thenReturn(order);
            when(orderDtoMapper.toCreateOrderResponse(order)).thenReturn(expectedResponse);

            // when
            OrderCreateResponse result = orderService.createOrder(userId, request);

            // then
            assertThat(result).isEqualTo(expectedResponse);
            verify(productUseCase).updateProductStock(1L, 2);
        }

        @Test
        @DisplayName("주문 생성 요청 시 존재하지 않는 상품으로 주문 시 BusinessException 이 리턴된다")
        void createOrder_ShouldThrowException_WhenProductNotFound() {
            // given
            Long userId = 1L;
            CreateOrderProductRequest orderProductRequest = createOrderProductRequest(1L, 2);
            CreateOrderRequest request = createOrderRequest(List.of(orderProductRequest));

            when(productUseCase.findProductsByProductIds(List.of(1L))).thenReturn(List.of());

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(userId, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.NOT_FOUND_PRODUCT);
        }

        @Test
        @DisplayName("주문 생성 요청 시 잔여 재고보다 많은 수량을 요청하면 BusinessException 이 리턴된다")
        void createOrder_ShouldThrowException_WhenOutOfStock() {
            // given
            Long userId = 1L;
            CreateOrderProductRequest orderProductRequest = createOrderProductRequest(1L, 5);
            CreateOrderRequest request = createOrderRequest(List.of(orderProductRequest));

            Product product = createProduct(1L, "Test Product", 1000, 100, 3);

            when(productUseCase.findProductsByProductIds(List.of(1L))).thenReturn(List.of(product));

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(userId, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.OUT_OF_STOCK);
        }

        @Test
        @DisplayName("주문 상품 가격 정보가 올바르게 계산되는지 검증")
        void createOrder_ShouldCalculatePricesCorrectly() {
            // given
            Long userId = 1L;
            CreateOrderProductRequest orderProductRequest = createOrderProductRequest(1L, 2);
            CreateOrderRequest request = createOrderRequest(List.of(orderProductRequest));

            Product product = createProduct(1L, "Test Product", 1000, 100, 10);

            OrderProduct orderProduct = createOrderProduct(1L, 2, 2000, 200, 1800);

            Order order = createOrder(1L, userId, List.of(orderProduct));

            OrderCreateResponse expectedResponse = OrderCreateResponse.builder()
                                                                      .orderId(1L)
                                                                      .build();

            when(productUseCase.findProductsByProductIds(List.of(1L))).thenReturn(List.of(product));
            when(orderDtoMapper.toOrderProduct(any())).thenReturn(orderProduct);
            when(orderUseCase.createOrder(userId, List.of(orderProduct))).thenReturn(order);
            when(orderDtoMapper.toCreateOrderResponse(order)).thenReturn(expectedResponse);

            // when
            OrderCreateResponse result = orderService.createOrder(userId, request);

            // then
            assertThat(result).isEqualTo(expectedResponse);
            assertThat(orderProductRequest.getSellingPrice()).isEqualTo(2000);
            assertThat(orderProductRequest.getDiscountAmount()).isEqualTo(200);
            assertThat(orderProductRequest.getPaymentPrice()).isEqualTo(1800);
        }

    }

    private CreateOrderProductRequest createOrderProductRequest(Long productId, Integer quantity) {
        return CreateOrderProductRequest.builder()
            .productId(productId)
            .quantity(quantity)
            .build();
    }

    private CreateOrderRequest createOrderRequest(List<CreateOrderProductRequest> orderProductLists) {
        return CreateOrderRequest.builder()
            .orderProductLists(orderProductLists)
            .build();
    }

    private Product createProduct(Long id, String productName, int sellingPrice, int discountAmount, int stock) {
        return Product.builder()
            .id(id)
            .productName(productName)
            .sellingPrice(sellingPrice)
            .discountAmount(discountAmount)
            .stock(stock)
            .build();
    }

    private Order createOrder(Long id, Long userId, List<OrderProduct> orderProducts) {
        return Order.builder()
            .id(id)
            .userId(userId)
            .orderProducts(orderProducts)
            .build();
    }

    private OrderProduct createOrderProduct(Long productId, Integer quantity, Integer sellingPrice, Integer discountAmount, Integer paymentPrice) {
        return OrderProduct.builder()
            .productId(productId)
            .quantity(quantity)
            .sellingPrice(sellingPrice)
            .discountAmount(discountAmount)
            .paymentPrice(paymentPrice)
            .build();
    }

}