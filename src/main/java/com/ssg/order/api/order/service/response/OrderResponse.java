package com.ssg.order.api.order.service.response;

import com.ssg.order.domain.domain.order.enumtype.OrderStatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "주문 response")
@Getter
@Builder
public class OrderResponse {
    @Schema(description = "주문 ID")
    private Long orderId;

    @Schema(description = "사용자 ID")
    private Long userId;

    @Schema(description = "주문 상태")
    private OrderStatusCode status;

    @Schema(description = "실구매가격")
    private Integer paymentPrice;

    @Schema(description = "판매가격")
    private Integer sellingPrice;

    @Schema(description = "할인금액")
    private Integer discountAmount;

    @Schema(description = "주문 요청 일시")
    private LocalDateTime createdAt;

    @Schema(description = "주문 수정 일시")
    private LocalDateTime updatedAt;
}
