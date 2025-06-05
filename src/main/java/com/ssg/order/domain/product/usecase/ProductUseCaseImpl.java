package com.ssg.order.domain.product.usecase;

import com.ssg.order.domain.common.annotation.UseCase;
import com.ssg.order.domain.product.Product;
import com.ssg.order.domain.product.repository.ProductReadRepository;
import com.ssg.order.domain.product.repository.ProductWriteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
class ProductUseCaseImpl implements ProductUseCase {
    private final ProductReadRepository productReadRepository;
    private final ProductWriteRepository productWriteRepository;

    @Override
    public List<Product> findAllProducts() {
        return productReadRepository.findAllProducts();
    }

    @Override
    public List<Product> findProductsByProductIds(List<Long> productIds) {
        return productReadRepository.findProductsByProductIds(productIds);
    }

    @Override
    public void updateProductStock(Long productId, int decreaseQuantity) {
        productWriteRepository.updateProductStock(productId, decreaseQuantity);
    }
}
