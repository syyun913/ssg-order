package com.ssg.order.api.order.service.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "주문 상품 response")
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderCreateProductResponse {
    @Schema(description = "주문상품 ID")
    private Long orderProductId;

    @Schema(description = "실구매가격")
    private Integer paymentPrice;

    @Schema(description = "주문 수량")
    private Integer quantity;
}
