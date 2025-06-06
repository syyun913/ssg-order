package com.ssg.order.api.order.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssg.order.api.order.service.model.OrderProductWithProduct;
import com.ssg.order.api.order.service.response.OrderProductsGetProductResponse;
import com.ssg.order.api.order.service.response.OrderProductsGetResponse;
import com.ssg.order.domain.domain.order.Order;
import com.ssg.order.domain.domain.order.OrderProduct;
import com.ssg.order.domain.domain.order.enumtype.OrderProductStatusCode;
import com.ssg.order.domain.domain.order.enumtype.OrderStatusCode;
import com.ssg.order.domain.domain.product.Product;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("주문 DTO 매퍼 테스트")
class OrderDtoMapperTest {
    private final OrderDtoMapper orderDtoMapper = new OrderDtoMapper();

    @Test
    @DisplayName("주문과 주문상품 정보를 응답 DTO로 변환한다")
    void toOrderProductsGetResponse_ShouldConvertToResponse() {
        // given
        Long orderId = 1L;
        Long userId = 1L;
        Long productId1 = 1L;
        Long productId2 = 2L;

        // 주문 상품 생성
        OrderProduct op1 = OrderProduct.builder()
            .id(1L)
            .orderId(orderId)
            .productId(productId1)
            .statusCode(OrderProductStatusCode.ORDER_COMPLETED)
            .quantity(2)
            .sellingPrice(1200)
            .discountAmount(200)
            .paymentPrice(1000)
            .build();

        OrderProduct op2 = OrderProduct.builder()
            .id(2L)
            .orderId(orderId)
            .productId(productId2)
            .statusCode(OrderProductStatusCode.ORDER_COMPLETED)
            .quantity(1)
            .sellingPrice(2500)
            .discountAmount(500)
            .paymentPrice(2000)
            .build();

        List<OrderProduct> orderProducts = List.of(op1, op2);

        // 주문 생성
        Order order = Order.builder()
            .id(orderId)
            .userId(userId)
            .statusCode(OrderStatusCode.ORDER_COMPLETED)
            .orderProducts(orderProducts)
            .paymentPrice(3000)
            .sellingPrice(3700)
            .discountAmount(700)
            .build();

        // 상품 정보 생성
        Product product1 = Product.builder()
            .id(productId1)
            .productName("상품1")
            .build();

        Product product2 = Product.builder()
            .id(productId2)
            .productName("상품2")
            .build();

        // 주문상품-상품 정보 생성
        OrderProductWithProduct opwp1 = OrderProductWithProduct.of(op1, product1);
        OrderProductWithProduct opwp2 = OrderProductWithProduct.of(op2, product2);
        List<OrderProductWithProduct> orderProductWithProducts = List.of(opwp1, opwp2);

        // when
        OrderProductsGetResponse result = orderDtoMapper.toOrderProductsGetResponse(order, orderProductWithProducts);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getPaymentPrice()).isEqualTo(3000);
        assertThat(result.getSellingPrice()).isEqualTo(3700);
        assertThat(result.getDiscountAmount()).isEqualTo(700);
        assertThat(result.getOrderProductLists()).hasSize(2);

        // 첫 번째 주문상품 검증
        OrderProductsGetProductResponse orderProduct1 = result.getOrderProductLists().get(0);
        assertThat(orderProduct1.getOrderProductId()).isEqualTo(1L);
        assertThat(orderProduct1.getProductId()).isEqualTo(productId1);
        assertThat(orderProduct1.getProductName()).isEqualTo("상품1");
        assertThat(orderProduct1.getQuantity()).isEqualTo(2);
        assertThat(orderProduct1.getPaymentPrice()).isEqualTo(1000);

        // 두 번째 주문상품 검증
        OrderProductsGetProductResponse orderProduct2 = result.getOrderProductLists().get(1);
        assertThat(orderProduct2.getOrderProductId()).isEqualTo(2L);
        assertThat(orderProduct2.getProductId()).isEqualTo(productId2);
        assertThat(orderProduct2.getProductName()).isEqualTo("상품2");
        assertThat(orderProduct2.getQuantity()).isEqualTo(1);
        assertThat(orderProduct2.getPaymentPrice()).isEqualTo(2000);
    }
} 