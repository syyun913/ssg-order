package com.ssg.order.infra.persistence.product.mapper;

import com.ssg.order.domain.domain.product.Product;
import com.ssg.order.infra.persistence.product.entity.ProductEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class ProductPersistenceMapper {
    public Product toDomain(ProductEntity productEntity) {
        if (ObjectUtils.isEmpty(productEntity)) {
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