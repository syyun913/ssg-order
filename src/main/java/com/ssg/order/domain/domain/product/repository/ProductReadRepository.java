package com.ssg.order.domain.domain.product.repository;

import com.ssg.order.domain.domain.product.Product;
import java.util.List;

public interface ProductReadRepository {
    List<Product> findAllProducts();
    List<Product> findProductsByProductIds(List<Long> productIds);
    Product getProductById(Long productId);
} 