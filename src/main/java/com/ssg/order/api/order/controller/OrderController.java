package com.ssg.order.api.order.controller;

import com.ssg.order.api.global.LoginUser;
import com.ssg.order.api.global.common.response.CommonResponse;
import com.ssg.order.api.order.service.OrderService;
import com.ssg.order.api.order.service.request.CreateOrderRequest;
import com.ssg.order.api.order.service.response.OrderCreateResponse;
import com.ssg.order.api.order.service.response.OrderProductCancelResponse;
import com.ssg.order.api.order.service.response.OrderProductsGetResponse;
import com.ssg.order.api.order.service.response.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "주문 서비스", description = "주문 관련 API")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "주문 생성 요청")
    @Parameter(name = "Authorization", description = "인가를 위한 토큰", in = ParameterIn.HEADER, required = true)
    @PostMapping
    public ResponseEntity<CommonResponse<OrderCreateResponse>> createOrder(
        @RequestBody @Valid CreateOrderRequest createOrderRequest,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        OrderCreateResponse orderCreateResponse = orderService.createOrder(loginUser.getId(), createOrderRequest);

        return ResponseEntity.ok(CommonResponse.of("주문이 성공적으로 생성되었습니다.", orderCreateResponse));
    }

    @Operation(summary = "주문 상품 조회")
    @Parameter(name = "Authorization", description = "인가를 위한 토큰", in = ParameterIn.HEADER, required = true)
    @GetMapping("/{order_id}/products")
    public ResponseEntity<CommonResponse<OrderProductsGetResponse>> getOrderProducts(
        @PathVariable("order_id") Long orderId,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        OrderProductsGetResponse order = orderService.getOrderWithOrderProducts(orderId, loginUser.getId());

        return ResponseEntity.ok(CommonResponse.of("주문 상품을 성공적으로 조회하였습니다.", order));
    }

    @Operation(summary = "주문 목록 조회")
    @Parameter(name = "Authorization", description = "인가를 위한 토큰", in = ParameterIn.HEADER, required = true)
    @GetMapping
    public ResponseEntity<CommonResponse<List<OrderResponse>>> getOrders(
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        List<OrderResponse> orders = orderService.getOrders(
            loginUser.getId());

        return ResponseEntity.ok(CommonResponse.of("주문을 성공적으로 조회하였습니다.", orders));
    }

    @Operation(summary = "주문 상품 취소")
    @Parameter(name = "Authorization", description = "인가를 위한 토큰", in = ParameterIn.HEADER, required = true)
    @PatchMapping("/{order_id}/products/{order_product_id}/cancellation")
    public ResponseEntity<CommonResponse<OrderProductCancelResponse>> cancelOrderProduct(
        @PathVariable("order_id") Long orderId,
        @PathVariable("order_product_id") Long orderProductId,
        @AuthenticationPrincipal LoginUser loginUser
    ) {
        OrderProductCancelResponse response = orderService.cancelOrderProduct(orderId, orderProductId, loginUser.getId());

        return ResponseEntity.ok(CommonResponse.of("상품 취소가 완료되었습니다.", response));
    }
}
