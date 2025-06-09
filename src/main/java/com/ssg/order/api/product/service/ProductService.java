package com.ssg.order.api.product.service;

import com.ssg.order.api.product.mapper.ProductDtoMapper;
import com.ssg.order.api.product.service.response.ProductResponse;
import com.ssg.order.domain.domain.product.usecase.ProductUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 도메인과 관련된 함수들이 정의됩니다.
 */
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductUseCase productUseCase;
    private final ProductDtoMapper productDtoMapper;

    /**
     * 전체 상품 목록을 조회한다.
     *
     * @return 전체 상품 목록
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> findAllProducts() {
        return productUseCase.findAllProducts().stream()
            .map(productDtoMapper::toProductResponse)
            .toList();
    }
}
