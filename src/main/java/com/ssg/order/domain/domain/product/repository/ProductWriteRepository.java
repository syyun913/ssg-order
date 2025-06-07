package com.ssg.order.domain.domain.product.repository;

public interface ProductWriteRepository {
    void updateProductStock(Long productId, int updateQuantity, boolean isIncrease);
} 