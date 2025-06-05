package com.ssg.order.api.order.mapper;

import com.ssg.order.api.order.service.request.CreateOrderProductRequest;
import com.ssg.order.api.order.service.response.CreateOrderResponse;
import com.ssg.order.api.order.service.response.OrderProductResponse;
import com.ssg.order.domain.domain.order.Order;
import com.ssg.order.domain.domain.order.OrderProduct;
import com.ssg.order.domain.domain.order.enumtype.OrderProductStatusCode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderDtoMapper {
    public OrderProduct toOrderProduct(CreateOrderProductRequest request) {
        return OrderProduct.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .statusCode(OrderProductStatusCode.ORDER_COMPLETED)
                .sellingPrice(request.getSellingPrice())
                .discountAmount(request.getDiscountAmount())
                .paymentPrice(request.getPaymentPrice())
                .build();
    }
    public CreateOrderResponse toCreateOrderResponse(Order order) {
        return CreateOrderResponse.builder()
                .orderId(order.getId())
                .paymentPrice(order.getPaymentPrice())
                .sellingPrice(order.getSellingPrice())
                .discountAmount(order.getDiscountAmount())
                .orderProductLists(toOrderProductResponses(order.getOrderProducts()))
                .build();
    }

    private List<OrderProductResponse> toOrderProductResponses(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .map(this::toOrderProductResponse)
                .toList();
    }

    private OrderProductResponse toOrderProductResponse(OrderProduct orderProduct) {
        return OrderProductResponse.builder()
                .orderProductId(orderProduct.getId())
                .paymentPrice(orderProduct.getPaymentPrice())
                .sellingPrice(orderProduct.getSellingPrice())
                .discountAmount(orderProduct.getDiscountAmount())
                .quantity(orderProduct.getQuantity())
                .build();
    }
} 