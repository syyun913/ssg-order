package com.ssg.order.api.product.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "상품 조회 response")
@Getter
@Builder
public class ProductResponse {
    @Schema(description = "상품 ID")
    private final Long id;
    @Schema(description = "상품명")
    private final String productName;
    @Schema(description = "판매가격")
    private final Integer sellingPrice;
    @Schema(description = "할인 금액")
    private final Integer discountAmount;
    @Schema(description = "재고")
    private final Integer stock;
} 