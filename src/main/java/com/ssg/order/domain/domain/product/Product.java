package com.ssg.order.domain.domain.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Product {
    private final Long id;
    private final String productName;
    private final Integer sellingPrice;
    private final Integer discountAmount;
    private final Integer stock;
} 