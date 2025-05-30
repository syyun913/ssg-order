package com.ssg.order.infra.persistence.order.entity;

import com.ssg.order.domain.order.enumtype.OrderProductStatusCode;
import com.ssg.order.infra.persistence.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "ORDER_PRODUCT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProductEntity extends BaseTimeEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, updatable = false)
    private Long orderId;

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
}
