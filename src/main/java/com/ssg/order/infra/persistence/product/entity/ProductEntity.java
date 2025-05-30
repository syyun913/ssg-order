package com.ssg.order.infra.persistence.product.entity;

import com.ssg.order.infra.persistence.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "PRODUCT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEntity extends BaseTimeEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "selling_price")
    private Integer sellingPrice;

    @Column(name = "discount_amount")
    private Integer discountAmount;

    @Column(name = "stock")
    private Integer stock;
}
