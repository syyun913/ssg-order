package com.ssg.order.api.order.mapper;

import com.ssg.order.api.order.service.model.OrderProductWithProduct;
import com.ssg.order.api.order.service.request.CreateOrderProductRequest;
import com.ssg.order.api.order.service.response.OrderCreateProductResponse;
import com.ssg.order.api.order.service.response.OrderCreateResponse;
import com.ssg.order.api.order.service.response.OrderProductCancelProductResponse;
import com.ssg.order.api.order.service.response.OrderProductCancelResponse;
import com.ssg.order.api.order.service.response.OrderProductsGetProductResponse;
import com.ssg.order.api.order.service.response.OrderProductsGetResponse;
import com.ssg.order.api.order.service.response.OrderResponse;
import com.ssg.order.domain.domain.order.Order;
import com.ssg.order.domain.domain.order.OrderProduct;
import com.ssg.order.domain.domain.order.enumtype.OrderProductStatusCode;
import java.util.List;
import org.springframework.stereotype.Component;

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
    public OrderCreateResponse toCreateOrderResponse(Order order) {
        return OrderCreateResponse.builder()
                                  .orderId(order.getId())
                                  .paymentPrice(order.getPaymentPrice())
                                  .sellingPrice(order.getSellingPrice())
                                  .discountAmount(order.getDiscountAmount())
                                  .orderProductLists(toOrderCreateProductResponses(order.getOrderProducts()))
                                  .build();
    }

    private List<OrderCreateProductResponse> toOrderCreateProductResponses(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .map(this::toOrderCreateProductResponse)
                .toList();
    }

    private OrderCreateProductResponse toOrderCreateProductResponse(OrderProduct orderProduct) {
        return OrderCreateProductResponse.builder()
                                         .orderProductId(orderProduct.getId())
                                         .paymentPrice(orderProduct.getPaymentPrice())
                                         .quantity(orderProduct.getQuantity())
                                         .build();
    }

    public OrderProductCancelResponse toOrderProductCancelResponse(Order order, OrderProductWithProduct orderProduct) {
        return OrderProductCancelResponse.builder()
                .orderId(order.getId())
                .paymentPrice(order.getPaymentPrice())
                .sellingPrice(order.getSellingPrice())
                .discountAmount(order.getDiscountAmount())
                .refundPrice(orderProduct.getPaymentPrice())
                .orderProduct(toOrderProductCancelProductResponse(orderProduct))
            .build();
    }

    public OrderProductCancelProductResponse toOrderProductCancelProductResponse(OrderProductWithProduct orderProduct) {
        return OrderProductCancelProductResponse.builder()
                .orderProductId(orderProduct.getOrderProductId())
                .productId(orderProduct.getProductId())
                .productName(orderProduct.getProductName())
                .status(orderProduct.getStatus())
                .paymentPrice(orderProduct.getPaymentPrice())
                .quantity(orderProduct.getQuantity())
                .build();
    }

    private List<OrderProductsGetProductResponse> toOrderProductsGetProductResponses(List<OrderProductWithProduct> orderProducts) {
        return orderProducts.stream()
                            .map(this::toOrderProductsGetProductResponse)
                            .toList();
    }

    private OrderProductsGetProductResponse toOrderProductsGetProductResponse(OrderProductWithProduct orderProduct) {
        return OrderProductsGetProductResponse.builder()
            .orderProductId(orderProduct.getOrderProductId()).productId(orderProduct.getProductId())
            .productName(orderProduct.getProductName())
            .status(orderProduct.getStatus())
            .paymentPrice(orderProduct.getPaymentPrice())
            .quantity(orderProduct.getQuantity())
            .build();
    }

    public OrderProductsGetResponse toOrderProductsGetResponse(Order order, List<OrderProductWithProduct> orderProductWithProducts) {
        return OrderProductsGetResponse.builder()
                .orderId(order.getId())
                .paymentPrice(order.getPaymentPrice())
                .sellingPrice(order.getSellingPrice())
                .discountAmount(order.getDiscountAmount())
                .orderProductLists(toOrderProductsGetProductResponses(orderProductWithProducts))
                .build();
    }

    public OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .status(order.getStatusCode())
                .paymentPrice(order.getPaymentPrice())
                .sellingPrice(order.getSellingPrice())
                .discountAmount(order.getDiscountAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
} 