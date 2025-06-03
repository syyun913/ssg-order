package com.ssg.order.api.product.service;

import com.ssg.order.domain.product.Product;
import com.ssg.order.domain.product.usecase.ProductUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductUseCase productUseCase;

    @Transactional(readOnly = true)
    public List<Product> findAllProducts() {
        return productUseCase.findAllProducts();
    }
}
