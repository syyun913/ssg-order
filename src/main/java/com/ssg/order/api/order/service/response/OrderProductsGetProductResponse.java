package com.ssg.order.api.order.service.response;

import com.ssg.order.domain.domain.order.enumtype.OrderProductStatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "주문 상품 response")
@Getter
@Builder
public class OrderProductsGetProductResponse {
    @Schema(description = "주문상품 ID")
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
}
