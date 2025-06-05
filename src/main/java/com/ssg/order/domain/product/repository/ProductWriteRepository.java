package com.ssg.order.domain.product.repository;

public interface ProductWriteRepository {
    void updateProductStock(Long productId, int decreaseQuantity);
} 