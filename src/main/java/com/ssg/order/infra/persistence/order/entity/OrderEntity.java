package com.ssg.order.infra.persistence.order.entity;

import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
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

    public void subtractPaymentPrice(Integer subtractPrice) {
        if ((this.paymentPrice - subtractPrice) < 0) {
            throw new BusinessException(BusinessErrorCode.ORDER_PRICE_CANNOT_BE_NEGATIVE,
                                        "orderId: " + this.id + ", subtractPaymentPrice: " + subtractPrice);
        }
        this.paymentPrice -= subtractPrice;
    }

    public void subtractSellingPrice(Integer subtractPrice) {
        if ((this.sellingPrice - subtractPrice) < 0) {
            throw new BusinessException(BusinessErrorCode.ORDER_PRICE_CANNOT_BE_NEGATIVE,
                                        "orderId: " + this.id + ", subtractSellingPrice: " + subtractPrice);
        }
        this.sellingPrice -= subtractPrice;
    }

    public void subtractDiscountAmount(Integer subtractAmount) {
        if ((this.discountAmount - subtractAmount) < 0) {
            throw new BusinessException(BusinessErrorCode.ORDER_PRICE_CANNOT_BE_NEGATIVE,
                                        "orderId: " + this.id + ", subtractDiscountAmount: " + subtractAmount);
        }
        this.discountAmount -= subtractAmount;
    }

    public void updateStatusCode(OrderStatusCode statusCode) {
        this.statusCode = statusCode;
    }
}
