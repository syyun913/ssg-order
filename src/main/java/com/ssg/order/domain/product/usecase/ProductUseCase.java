package com.ssg.order.domain.product.usecase;

import com.ssg.order.domain.product.Product;
import java.util.List;

public interface ProductUseCase {
    List<Product> findAllProducts();
}
