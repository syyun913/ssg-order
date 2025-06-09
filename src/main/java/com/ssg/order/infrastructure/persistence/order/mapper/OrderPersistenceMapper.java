package com.ssg.order.infrastructure.persistence.order.mapper;

import com.ssg.order.domain.domain.order.Order;
import com.ssg.order.domain.domain.order.OrderProduct;
import com.ssg.order.infrastructure.persistence.order.entity.OrderEntity;
import com.ssg.order.infrastructure.persistence.order.entity.OrderProductEntity;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class OrderPersistenceMapper {
    public OrderEntity toEntity(Order order) {
        if (ObjectUtils.isEmpty(order)) {
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
        if (ObjectUtils.isEmpty(orderEntity)) {
            return null;
        }
        List<OrderProduct> orderProducts = null;
        if (!ObjectUtils.isEmpty(orderProductEntities)) {
            orderProducts = orderProductEntities.stream()
                    .map(this::toDomain)
                    .toList();
        }
        return Order.builder()
                .id(orderEntity.getId())
                .userId(orderEntity.getUserId())
                .statusCode(orderEntity.getStatusCode())
                .paymentPrice(orderEntity.getPaymentPrice())
                .sellingPrice(orderEntity.getSellingPrice())
                .discountAmount(orderEntity.getDiscountAmount())
                .createdAt(orderEntity.getCreatedAt())
                .updatedAt(orderEntity.getUpdatedAt())
                .orderProducts(orderProducts)
                .build();
    }

    public OrderProductEntity toEntity(OrderProduct orderProduct, OrderEntity orderEntity) {
        if (ObjectUtils.isEmpty(orderProduct)) {
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
        if (ObjectUtils.isEmpty(orderProductEntity)) {
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