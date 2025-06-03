package com.ssg.order.infra.persistence.product.mapper;

import com.ssg.order.domain.product.Product;
import com.ssg.order.infra.persistence.product.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductPersistenceMapper {
    public Product toDomain(ProductEntity productEntity) {
        if (productEntity == null) {
            return null;
        }
        return Product.builder()
                .id(productEntity.getId())
                .productName(productEntity.getProductName())
                .sellingPrice(productEntity.getSellingPrice())
                .discountAmount(productEntity.getDiscountAmount())
                .stock(productEntity.getStock())
                .build();
    }
} 