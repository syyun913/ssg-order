package com.ssg.order.infra.persistence.order.entity;

import com.ssg.order.domain.domain.order.enumtype.OrderStatusCode;
import com.ssg.order.infra.persistence.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "ORDERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity extends BaseTimeEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "status_code", nullable = false, columnDefinition = "varchar(30)")
    @Enumerated(EnumType.STRING)
    private OrderStatusCode statusCode;

    @Column(name = "payment_price", nullable = false)
    private Integer paymentPrice;

    @Column(name = "selling_price", nullable = false)
    private Integer sellingPrice;

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;

    @Builder
    public OrderEntity(Long id, Long userId, OrderStatusCode statusCode, Integer paymentPrice, Integer sellingPrice, Integer discountAmount) {
        this.id = id;
        this.userId = userId;
        this.statusCode = statusCode;
        this.paymentPrice = paymentPrice;
        this.sellingPrice = sellingPrice;
        this.discountAmount = discountAmount;
    }
}
