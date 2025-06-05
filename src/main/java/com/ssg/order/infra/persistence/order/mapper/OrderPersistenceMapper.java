package com.ssg.order.infra.persistence.order.mapper;

import com.ssg.order.domain.order.Order;
import com.ssg.order.domain.order.OrderProduct;
import com.ssg.order.infra.persistence.order.entity.OrderEntity;
import com.ssg.order.infra.persistence.order.entity.OrderProductEntity;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderPersistenceMapper {
    public OrderEntity toEntity(Order order) {
        if (order == null) {
            return null;
        }
        return OrderEntity.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .statusCode(order.getStatusCode())
                .paymentPrice(order.getPaymentPrice())
                .sellingPrice(order.getSellingPrice())
                .discountAmount(order.getDiscountAmount())
                .build();
    }

    public Order toDomain(OrderEntity orderEntity, List<OrderProductEntity> orderProductEntities) {
        if (orderEntity == null) {
            return null;
        }
        return Order.builder()
                .id(orderEntity.getId())
                .userId(orderEntity.getUserId())
                .statusCode(orderEntity.getStatusCode())
                .paymentPrice(orderEntity.getPaymentPrice())
                .sellingPrice(orderEntity.getSellingPrice())
                .discountAmount(orderEntity.getDiscountAmount())
                .orderProducts(orderProductEntities.stream()
                        .map(this::toDomain)
                        .toList())
                .build();
    }

    public OrderProductEntity toEntity(OrderProduct orderProduct, OrderEntity orderEntity) {
        if (orderProduct == null) {
            return null;
        }
        return OrderProductEntity.builder()
                .id(orderProduct.getId())
                .order(orderEntity)
                .productId(orderProduct.getProductId())
                .statusCode(orderProduct.getStatusCode())
                .quantity(orderProduct.getQuantity())
                .paymentPrice(orderProduct.getPaymentPrice())
                .sellingPrice(orderProduct.getSellingPrice())
                .discountAmount(orderProduct.getDiscountAmount())
                .build();
    }

    public OrderProduct toDomain(OrderProductEntity orderProductEntity) {
        if (orderProductEntity == null) {
            return null;
        }
        return OrderProduct.builder()
                .id(orderProductEntity.getId())
                .orderId(orderProductEntity.getOrder().getId())
                .productId(orderProductEntity.getProductId())
                .statusCode(orderProductEntity.getStatusCode())
                .quantity(orderProductEntity.getQuantity())
                .paymentPrice(orderProductEntity.getPaymentPrice())
                .sellingPrice(orderProductEntity.getSellingPrice())
                .discountAmount(orderProductEntity.getDiscountAmount())
                .build();
    }
} 