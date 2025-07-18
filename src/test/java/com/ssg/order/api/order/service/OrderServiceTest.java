package com.ssg.order.api.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ssg.order.api.order.mapper.OrderDtoMapper;
import com.ssg.order.api.order.service.request.CreateOrderProductRequest;
import com.ssg.order.api.order.service.request.CreateOrderRequest;
import com.ssg.order.api.order.service.response.OrderCreateResponse;
import com.ssg.order.api.order.service.response.OrderProductCancelResponse;
import com.ssg.order.api.order.service.response.OrderProductsGetProductResponse;
import com.ssg.order.api.order.service.response.OrderProductsGetResponse;
import com.ssg.order.api.order.service.response.OrderResponse;
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
            verify(productUseCase).updateProductStock(1L, 2, false);
        }

        @Test
        @DisplayName("주문 생성 요청 시 존재하지 않는 상품으로 주문 시 BusinessException 이 리턴된다")
        void createOrder_ShouldThrowException_WhenProductNotFound() {
            // given
            Long userId = 1L;
            CreateOrderProductRequest orderProductRequest = createOrderProductRequest(1L, 2);
            CreateOrderRequest request = createOrderRequest(List.of(orderProductRequest));

            when(productUseCase.findProductsByProductIds(List.of(1L))).thenReturn(List.of());

            // when
            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> orderService.createOrder(userId, request)
            );

            // then
            assertAll(
                () -> assertEquals(BusinessErrorCode.NOT_FOUND_PRODUCT, exception.getErrorCode()),
                () -> assertEquals("상품을 조회할 수 없습니다.", exception.getMessage())
            );
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

            // when
            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> orderService.createOrder(userId, request)
            );

            // then
            assertAll(
                () -> assertEquals(BusinessErrorCode.OUT_OF_STOCK, exception.getErrorCode()),
                () -> assertEquals("상품 재고가 부족합니다.", exception.getMessage())
            );
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

    @Nested
    @DisplayName("주문(주문상품 포함) 조회")
    class GetOrderWithOrderProducts {
        @Test
        @DisplayName("주문(주문상품 포함) 조회 시 주문과 주문상품 정보를 조회한다")
        void getOrderWithOrderProducts_ShouldReturnOrderWithProducts() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            Long productId1 = 1L;
            Long productId2 = 2L;

            // 주문 상품 생성
            OrderProduct op1 = createOrderProduct(1L, orderId, productId1, 1000, 1200, 200, 2);
            OrderProduct op2 = createOrderProduct(2L, orderId, productId2, 2000, 2500, 500, 1);
            List<OrderProduct> orderProducts = List.of(op1, op2);

            // 주문 생성
            Order order = createOrder(orderId, userId, orderProducts, 3000, 3700, 700);

            // 상품 정보 생성
            Product product1 = createProduct(productId1, "상품1", 600, 100, 10);
            Product product2 = createProduct(productId2, "상품2", 2500, 500, 5);
            List<Product> products = List.of(product1, product2);

            // Mock 설정
            when(orderUseCase.getOrderWithOrderProducts(orderId, userId)).thenReturn(order);
            when(productUseCase.findProductsByProductIds(List.of(productId1, productId2))).thenReturn(products);

            // OrderProductsGetResponse 생성
            OrderProductsGetResponse expectedResponse = OrderProductsGetResponse.builder()
                .orderId(orderId)
                .paymentPrice(3000)
                .sellingPrice(3700)
                .discountAmount(700)
                .orderProductLists(List.of(
                    OrderProductsGetProductResponse.builder()
                        .orderProductId(1L)
                        .productId(productId1)
                        .productName("상품1")
                        .quantity(2)
                        .paymentPrice(1000)
                        .build(),
                    OrderProductsGetProductResponse.builder()
                        .orderProductId(2L)
                        .productId(productId2)
                        .productName("상품2")
                        .quantity(1)
                        .paymentPrice(2000)
                        .build()
                ))
                .build();

            when(orderDtoMapper.toOrderProductsGetResponse(any(), any())).thenReturn(expectedResponse);

            // when
            OrderProductsGetResponse result = orderService.getOrderWithOrderProducts(orderId, userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getOrderId()).isEqualTo(orderId);
            assertThat(result.getOrderProductLists()).hasSize(2);
            assertThat(result.getPaymentPrice()).isEqualTo(3000);
            assertThat(result.getSellingPrice()).isEqualTo(3700);
            assertThat(result.getDiscountAmount()).isEqualTo(700);

            // 주문 상품 정보 검증
            var orderProduct1 = result.getOrderProductLists().get(0);
            assertThat(orderProduct1.getProductId()).isEqualTo(productId1);
            assertThat(orderProduct1.getProductName()).isEqualTo("상품1");
            assertThat(orderProduct1.getQuantity()).isEqualTo(2);
            assertThat(orderProduct1.getPaymentPrice()).isEqualTo(1000);

            var orderProduct2 = result.getOrderProductLists().get(1);
            assertThat(orderProduct2.getProductId()).isEqualTo(productId2);
            assertThat(orderProduct2.getProductName()).isEqualTo("상품2");
            assertThat(orderProduct2.getQuantity()).isEqualTo(1);
            assertThat(orderProduct2.getPaymentPrice()).isEqualTo(2000);
        }

        @Test
        @DisplayName("주문(주문상품 포함) 조회 시 상품이 존재하지 않으면 BusinessException이 리턴된다")
        void getOrderWithOrderProducts_WithNonExistentProduct_ShouldThrowException() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            Long productId = 1L;

            // 주문 상품 생성
            OrderProduct orderProduct = createOrderProduct(1L, orderId, productId, 1000, 1200, 200, 2);
            List<OrderProduct> orderProducts = List.of(orderProduct);

            // 주문 생성
            Order order = createOrder(orderId, userId, orderProducts, 1000, 1200, 200);

            // Mock 설정
            when(orderUseCase.getOrderWithOrderProducts(orderId, userId)).thenReturn(order);
            when(productUseCase.findProductsByProductIds(List.of(productId))).thenReturn(List.of());

            // when
            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> orderService.getOrderWithOrderProducts(orderId, userId)
            );

            // then
            assertAll(
                () -> assertEquals(BusinessErrorCode.NOT_FOUND_PRODUCT, exception.getErrorCode()),
                () -> assertEquals("상품을 조회할 수 없습니다.", exception.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("주문 목록 조회")
    class GetOrders {
        @Test
        @DisplayName("사용자 ID로 주문 목록 조회 시 주문 목록을 리턴한다")
        void getOrders_ShouldReturnOrderList() {
            // given
            Long userId = 1L;
            Order order1 = createOrder(1L, userId, List.of());
            Order order2 = createOrder(2L, userId, List.of());
            List<Order> orders = List.of(order1, order2);

            OrderResponse response1 = createOrderResponse(1L);
            OrderResponse response2 = createOrderResponse(2L);
            List<OrderResponse> expectedResponses = List.of(response1, response2);

            when(orderUseCase.findOrdersByUserId(userId, true)).thenReturn(orders);
            when(orderDtoMapper.toOrderResponse(order1)).thenReturn(response1);
            when(orderDtoMapper.toOrderResponse(order2)).thenReturn(response2);

            // when
            List<OrderResponse> result = orderService.getOrders(userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expectedResponses);
        }

        @Test
        @DisplayName("사용자 ID로 주문 목록 조회 시 주문이 없으면 빈 배열이 리턴된다")
        void getOrders_WithNoOrders_ShouldReturnEmptyList() {
            // given
            Long userId = 1L;
            when(orderUseCase.findOrdersByUserId(userId, true)).thenReturn(List.of());

            // when
            List<OrderResponse> result = orderService.getOrders(userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("주문 상품 취소")
    class CancelOrderProduct {
        @Test
        @DisplayName("주문 상품 취소 시 주문 상품 상태가 변경되고 주문 금액이 차감되며 상품 재고가 원복된다")
        void cancelOrderProduct_ShouldSucceed() {
            // given
            Long orderId = 1L;
            Long orderProductId = 1L;
            Long userId = 1L;
            Long productId = 1L;

            // 취소될 주문 상품 생성
            OrderProduct orderProduct = createOrderProduct(orderProductId, orderId, productId, 2000, 2500, 500, 2);
            
            // 취소 후 업데이트될 주문 생성
            Order updatedOrder = createOrder(orderId, userId, List.of(), 1000, 1200, 200);
            
            // 상품 정보 생성
            Product product = createProduct(productId, "Test Product", 1000, 100, 10);

            // Mock 설정
            when(orderUseCase.cancelOrderProduct(orderId, orderProductId, userId)).thenReturn(orderProduct);
            when(orderUseCase.subtractOrderPrice(
                orderId,
                userId,
                orderProduct.getPaymentPrice(),
                orderProduct.getSellingPrice(),
                orderProduct.getDiscountAmount()
            )).thenReturn(updatedOrder);
            when(productUseCase.getProductById(productId)).thenReturn(product);

            // OrderProductCancelResponse 생성
            OrderProductCancelResponse expectedResponse = OrderProductCancelResponse.builder()
                .orderId(orderId)
                .paymentPrice(1000)
                .sellingPrice(1200)
                .discountAmount(200)
                .build();

            when(orderDtoMapper.toOrderProductCancelResponse(any(), any())).thenReturn(expectedResponse);

            // when
            OrderProductCancelResponse result = orderService.cancelOrderProduct(orderId, orderProductId, userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(expectedResponse);
            
            // 주문 상품 취소 및 주문 금액 차감 검증
            verify(orderUseCase).cancelOrderProduct(orderId, orderProductId, userId);
            verify(orderUseCase).subtractOrderPrice(
                orderId,
                userId,
                orderProduct.getPaymentPrice(),
                orderProduct.getSellingPrice(),
                orderProduct.getDiscountAmount()
            );
            
            // 상품 재고 원복 검증
            verify(productUseCase).updateProductStock(productId, orderProduct.getQuantity(), true);
        }

        @Test
        @DisplayName("존재하지 않는 주문 ID로 취소 요청 시 BusinessException이 발생한다")
        void cancelOrderProduct_WithNonExistentOrder_ShouldThrowException() {
            // given
            Long orderId = 999L;
            Long orderProductId = 1L;
            Long userId = 1L;

            when(orderUseCase.cancelOrderProduct(orderId, orderProductId, userId))
                .thenThrow(new BusinessException(BusinessErrorCode.NOT_FOUND_ORDER));

            // when
            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> orderService.cancelOrderProduct(orderId, orderProductId, userId)
            );

            // then
            assertAll(
                () -> assertEquals(BusinessErrorCode.NOT_FOUND_ORDER, exception.getErrorCode()),
                () -> assertEquals("주문 정보를 조회할 수 없습니다.", exception.getMessage())
            );
        }

        @Test
        @DisplayName("존재하지 않는 주문 상품 ID로 취소 요청 시 BusinessException이 발생한다")
        void cancelOrderProduct_WithNonExistentOrderProduct_ShouldThrowException() {
            // given
            Long orderId = 1L;
            Long orderProductId = 999L;
            Long userId = 1L;

            when(orderUseCase.cancelOrderProduct(orderId, orderProductId, userId))
                .thenThrow(new BusinessException(BusinessErrorCode.NOT_FOUND_ORDER_PRODUCT));

            // when
            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> orderService.cancelOrderProduct(orderId, orderProductId, userId)
            );

            // then
            assertAll(
                () -> assertEquals(BusinessErrorCode.NOT_FOUND_ORDER_PRODUCT, exception.getErrorCode()),
                () -> assertEquals("주문 상품을 찾을 수 없습니다.", exception.getMessage())
            );
        }

        @Test
        @DisplayName("이미 취소된 주문 상품에 대한 재취소 요청 시 BusinessException이 발생한다")
        void cancelOrderProduct_WithAlreadyCanceledProduct_ShouldThrowException() {
            // given
            Long orderId = 1L;
            Long orderProductId = 1L;
            Long userId = 1L;

            when(orderUseCase.cancelOrderProduct(orderId, orderProductId, userId))
                .thenThrow(new BusinessException(BusinessErrorCode.ALREADY_CANCELED_ORDER_PRODUCT));

            // when
            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> orderService.cancelOrderProduct(orderId, orderProductId, userId)
            );

            // then
            assertAll(
                () -> assertEquals(BusinessErrorCode.ALREADY_CANCELED_ORDER_PRODUCT, exception.getErrorCode()),
                () -> assertEquals("이미 취소된 주문 상품입니다.", exception.getMessage())
            );
        }

        @Test
        @DisplayName("주문 상품 취소 시 해당 상품의 재고가 정확히 복구되어야 한다")
        void cancelOrderProduct_ShouldRestoreProductStock() {
            // given
            Long orderId = 1L;
            Long orderProductId = 1L;
            Long userId = 1L;
            Long productId = 1L;
            Integer orderQuantity = 3;
            Integer originalStock = 10;

            // 취소될 주문 상품 생성
            OrderProduct orderProduct = createOrderProduct(orderProductId, orderId, productId, 2000, 2500, 500, orderQuantity);
            
            // 취소 후 업데이트될 주문 생성
            Order updatedOrder = createOrder(orderId, userId, List.of(), 1000, 1200, 200);
            
            // 상품 정보 생성 (재고 정보 포함)
            Product product = createProduct(productId, "Test Product", 1000, 100, originalStock);

            // Mock 설정
            when(orderUseCase.cancelOrderProduct(orderId, orderProductId, userId)).thenReturn(orderProduct);
            when(orderUseCase.subtractOrderPrice(
                orderId,
                userId,
                orderProduct.getPaymentPrice(),
                orderProduct.getSellingPrice(),
                orderProduct.getDiscountAmount()
            )).thenReturn(updatedOrder);
            when(productUseCase.getProductById(productId)).thenReturn(product);

            OrderProductCancelResponse expectedResponse = OrderProductCancelResponse.builder()
                .orderId(orderId)
                .paymentPrice(1000)
                .sellingPrice(1200)
                .discountAmount(200)
                .build();

            when(orderDtoMapper.toOrderProductCancelResponse(any(), any())).thenReturn(expectedResponse);

            // when
            orderService.cancelOrderProduct(orderId, orderProductId, userId);

            // then
            // 재고 복구 검증
            verify(productUseCase).updateProductStock(
                eq(productId),    // 상품 ID가 일치하는지
                eq(orderQuantity), // 취소된 수량만큼 재고가 복구되는지
                eq(true)          // 재고 증가(true)로 호출되는지
            );
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

    private OrderProduct createOrderProduct(Long id, Long orderId, Long productId, Integer sellingPrice, Integer discountAmount, Integer paymentPrice, Integer quantity) {
        return OrderProduct.builder()
            .id(id)
            .orderId(orderId)
            .productId(productId)
            .sellingPrice(sellingPrice)
            .discountAmount(discountAmount)
            .paymentPrice(paymentPrice)
            .quantity(quantity)
            .build();
    }

    private Order createOrder(Long id, Long userId, List<OrderProduct> orderProducts) {
        return Order.builder()
            .id(id)
            .userId(userId)
            .orderProducts(orderProducts)
            .build();
    }

    private Order createOrder(Long id, Long userId, List<OrderProduct> orderProducts, int paymentPrice, int sellingPrice, int discountAmount) {
        return Order.builder()
                    .id(id)
                    .userId(userId)
                    .orderProducts(orderProducts)
                    .paymentPrice(paymentPrice)
                    .sellingPrice(sellingPrice)
                    .discountAmount(discountAmount)
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

    private OrderResponse createOrderResponse(Long orderId) {
        return OrderResponse.builder()
                            .orderId(orderId)
                            .build();
    }

}