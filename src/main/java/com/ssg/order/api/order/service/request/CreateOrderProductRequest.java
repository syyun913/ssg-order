package com.ssg.order.api.order.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "주문 생성 요청 상품 request")
@Getter
@Builder
public class CreateOrderProductRequest {
    @Schema(description = "상품 ID")
    @NotNull
    private Long productId;

    @Schema(description = "상품 수량")
    @NotNull
    private Integer quantity;

    @Schema(hidden = true)
    @JsonIgnore
    @Setter
    private Integer paymentPrice;

    @Schema(hidden = true)
    @JsonIgnore
    @Setter
    private Integer sellingPrice;

    @Schema(hidden = true)
    @JsonIgnore
    @Setter
    private Integer discountAmount;
}
