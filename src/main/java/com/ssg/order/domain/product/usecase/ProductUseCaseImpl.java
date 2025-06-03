package com.ssg.order.domain.product.usecase;

import com.ssg.order.domain.common.annotation.UseCase;
import com.ssg.order.domain.product.Product;
import com.ssg.order.domain.product.repository.ProductReadRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;

@UseCase
@RequiredArgsConstructor
class ProductUseCaseImpl implements ProductUseCase {
    private final ProductReadRepository productReadRepository;

    @Override
    public List<Product> findAllProducts() {
        return productReadRepository.findAllProducts();
    }
}
