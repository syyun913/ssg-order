package com.ssg.order.api.product.mapper;

import com.ssg.order.api.product.service.response.ProductResponse;
import com.ssg.order.domain.domain.product.Product;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class ProductDtoMapper {
    public ProductResponse toProductResponse(Product product) {
        if (ObjectUtils.isEmpty(product)) {
            return null;
        }
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .sellingPrice(product.getSellingPrice())
                .discountAmount(product.getDiscountAmount())
                .stock(product.getStock())
                .build();
    }
} 