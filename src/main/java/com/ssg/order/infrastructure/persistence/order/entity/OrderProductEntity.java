package com.ssg.order.infrastructure.persistence.order.entity;

import com.ssg.order.domain.domain.order.enumtype.OrderProductStatusCode;
import com.ssg.order.infrastructure.persistence.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "ORDER_PRODUCT", indexes = {
    @Index(name = "idx-order_id", columnList = "order_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProductEntity extends BaseTimeEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    private OrderEntity order;

    @Column(name = "product_id", nullable = false, updatable = false)
    private Long productId;

    @Column(name = "status_code", nullable = false, columnDefinition = "varchar(30)")
    @Enumerated(EnumType.STRING)
    private OrderProductStatusCode statusCode;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "payment_price", nullable = false)
    private Integer paymentPrice;

    @Column(name = "selling_price", nullable = false)
    private Integer sellingPrice;

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;

    @Builder
    public OrderProductEntity(Long id, OrderEntity order, Long productId, OrderProductStatusCode statusCode,
                              Integer quantity, Integer paymentPrice, Integer sellingPrice, Integer discountAmount) {
        this.id = id;
        this.order = order;
        this.productId = productId;
        this.statusCode = statusCode;
        this.quantity = quantity;
        this.paymentPrice = paymentPrice;
        this.sellingPrice = sellingPrice;
        this.discountAmount = discountAmount;
    }

    public void updateStatusCode(OrderProductStatusCode statusCode) {
        this.statusCode = statusCode;
    }
}
