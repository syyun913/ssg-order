package com.ssg.order.infra.persistence.order.entity;

import com.ssg.order.infra.persistence.common.entity.BaseTimeEntity;
import com.ssg.order.infra.persistence.order.converter.OrderStatusCodeConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "ORDERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity extends BaseTimeEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "status_code")
    @Convert(converter = OrderStatusCodeConverter.class)
    private String statusCode;

    @Column(name = "payment_price")
    private Integer paymentPrice;

    @Column(name = "selling_price")
    private Integer sellingPrice;

    @Column(name = "discount_amount")
    private Integer discountAmount;
}
