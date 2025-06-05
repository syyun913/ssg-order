package com.ssg.order.domain.domain.product.usecase;

import com.ssg.order.domain.domain.product.Product;
import java.util.List;

public interface ProductUseCase {
    List<Product> findAllProducts();
    List<Product> findProductsByProductIds(List<Long> productIds);
    void updateProductStock(Long productId, int decreaseQuantity);
}
