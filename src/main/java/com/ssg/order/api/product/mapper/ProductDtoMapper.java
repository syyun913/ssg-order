package com.ssg.order.api.product.mapper;

import com.ssg.order.api.product.controller.response.ProductResponse;
import com.ssg.order.domain.product.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoMapper {
    public ProductResponse domainToProductResponse(Product product) {
        if (product == null) {
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