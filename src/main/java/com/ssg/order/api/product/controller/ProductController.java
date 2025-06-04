package com.ssg.order.api.product.controller;

import com.ssg.order.api.global.common.response.CommonResponse;
import com.ssg.order.api.product.service.ProductService;
import com.ssg.order.api.product.service.response.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "상품 서비스", description = "상품 관련 API")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "상품 목록 조회", description = "전체 상품 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<CommonResponse<List<ProductResponse>>> retrieveProducts(@RequestHeader("Authorization") String authorizationHeader) {
        List<ProductResponse> productResponses = productService.findAllProducts();

        return ResponseEntity.ok(CommonResponse.of("상품을 성공적으로 조회하였습니다.", productResponses));
    }
}
