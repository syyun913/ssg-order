package com.ssg.order.api.order.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "주문상품 response")
@Getter
@Builder
public class OrderProductResponse {
    @Schema(description = "주문상품 ID")
    private Long orderProductId;

    @Schema(description = "실구매가격")
    private Integer paymentPrice;

    @Schema(description = "구매가격")
    private Integer sellingPrice;

    @Schema(description = "할인금액")
    private Integer discountAmount;
    
    @Schema(description = "주문 수량")
    private Integer quantity;
}
