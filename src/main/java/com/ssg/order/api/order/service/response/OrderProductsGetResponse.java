package com.ssg.order.api.order.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "주문 상품 조회 response")
@Getter
@Builder
public class OrderProductsGetResponse {
    @Schema(description = "주문 ID")
    private Long orderId;

    @Schema(description = "실구매가격")
    private Integer paymentPrice;

    @Schema(description = "판매가격")
    private Integer sellingPrice;

    @Schema(description = "할인금액")
    private Integer discountAmount;

    @Schema(description = "주문 상품 목록")
    private List<OrderProductsGetProductResponse> orderProductLists;
}
