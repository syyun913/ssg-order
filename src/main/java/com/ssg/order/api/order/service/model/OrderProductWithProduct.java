package com.ssg.order.api.order.service.model;

import com.ssg.order.domain.domain.order.OrderProduct;
import com.ssg.order.domain.domain.order.enumtype.OrderProductStatusCode;
import com.ssg.order.domain.domain.product.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderProductWithProduct {
    @Schema(description = "주문 상품 ID")
    private Long orderProductId;

    @Schema(description = "상품 ID")
    private Long productId;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "주문 상품 상태")
    private OrderProductStatusCode status;

    @Schema(description = "실구매가격")
    private Integer paymentPrice;

    @Schema(description = "주문 수량")
    private Integer quantity;

    public static OrderProductWithProduct of(OrderProduct orderProduct, Product product) {
        return OrderProductWithProduct.builder()
                .orderProductId(orderProduct.getId())
                .productId(product.getId())
                .productName(product.getProductName())
                .status(orderProduct.getStatusCode())
                .paymentPrice(orderProduct.getPaymentPrice())
                .quantity(orderProduct.getQuantity())
                .build();
    }
}
