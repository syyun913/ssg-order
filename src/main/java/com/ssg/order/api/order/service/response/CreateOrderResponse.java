package com.ssg.order.api.order.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "주문 생성 response")
@Getter
@Builder
public class CreateOrderResponse {

    @Schema(description = "주문 ID")
    private Long orderId;

    @Schema(description = "실구매가격")
    private Integer paymentPrice;

    @Schema(description = "구매가격")
    private Integer sellingPrice;

    @Schema(description = "할인금액")
    private Integer discountAmount;

    @Schema(description = "주문상품 목록")
    private List<OrderProductResponse> orderProductLists;
}
