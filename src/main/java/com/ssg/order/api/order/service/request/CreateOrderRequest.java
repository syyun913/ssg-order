package com.ssg.order.api.order.service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "주문 생성 request")
@Getter
@Builder
public class CreateOrderRequest {
    @Schema(description = "주문 생성 요청 상품 목록")
    @Valid
    @NotEmpty
    @Builder.Default
    List<CreateOrderProductRequest> orderProductLists = new ArrayList<>();
}
