package com.ssg.order.domain.product.repository;

import com.ssg.order.domain.product.Product;
import java.util.List;

public interface ProductReadRepository {
    List<Product> findAllProducts();
} 