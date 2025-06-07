package com.ssg.order.infra.persistence.product.entity;

import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
import com.ssg.order.infra.persistence.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@DynamicUpdate
@Table(name = "PRODUCT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEntity extends BaseTimeEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false, columnDefinition = "VARCHAR(255)")
    private String productName;

    @Column(name = "selling_price", nullable = false)
    private Integer sellingPrice;

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Builder
    public ProductEntity(Long id, String productName, Integer sellingPrice, Integer discountAmount, Integer stock) {
        this.id = id;
        this.productName = productName;
        this.sellingPrice = sellingPrice;
        this.discountAmount = discountAmount;
        this.stock = stock;
    }

    public void decreaseStock(int decreaseQuantity) {
        if (this.stock < decreaseQuantity) {
            throw new BusinessException(BusinessErrorCode.OUT_OF_STOCK,
                                        "productId: " + this.id + ", productName: " + this.productName);
        }
        this.stock -= decreaseQuantity;
    }

    public void increaseStock(int increaseQuantity) {
        this.stock += increaseQuantity;
    }
}
