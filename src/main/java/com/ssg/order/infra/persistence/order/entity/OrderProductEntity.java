package com.ssg.order.infra.persistence.order.entity;

import com.ssg.order.infra.persistence.common.entity.BaseTimeEntity;
import com.ssg.order.infra.persistence.order.converter.OrderProductStatusCodeConverter;
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

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "status_code")
    @Convert(converter = OrderProductStatusCodeConverter.class)
    private String statusCode;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "payment_price")
    private Integer paymentPrice;

    @Column(name = "selling_price")
    private Integer sellingPrice;

    @Column(name = "discount_amount")
    private Integer discountAmount;
}
