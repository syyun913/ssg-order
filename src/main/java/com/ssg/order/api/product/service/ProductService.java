package com.ssg.order.api.product.service;

import com.ssg.order.api.product.mapper.ProductDtoMapper;
import com.ssg.order.api.product.service.response.ProductResponse;
import com.ssg.order.domain.domain.product.usecase.ProductUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductUseCase productUseCase;
    private final ProductDtoMapper productDtoMapper;

    @Transactional(readOnly = true)
    public List<ProductResponse> findAllProducts() {
        return productUseCase.findAllProducts().stream()
            .map(productDtoMapper::toProductResponse)
            .toList();
    }
}
