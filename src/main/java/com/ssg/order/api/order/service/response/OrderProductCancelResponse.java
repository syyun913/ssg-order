package com.ssg.order.api.order.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "주문 상품 취소 response")
@Getter
@Builder
public class OrderProductCancelResponse {

    @Schema(description = "주문 ID")
    private Long orderId;

    @Schema(description = "잔여 실구매가격")
    private Integer paymentPrice;

    @Schema(description = "잔여 구매가격")
    private Integer sellingPrice;

    @Schema(description = "잔여 할인금액")
    private Integer discountAmount;

    @Schema(description = "환불금액")
    private Integer refundPrice;

    @Schema(description = "취소된 상품 정보")
    private OrderProductCancelProductResponse orderProduct;
}
