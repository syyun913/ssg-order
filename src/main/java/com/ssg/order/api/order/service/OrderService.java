package com.ssg.order.api.order.service;

import com.ssg.order.api.order.mapper.OrderDtoMapper;
import com.ssg.order.api.order.service.model.OrderProductWithProduct;
import com.ssg.order.api.order.service.request.CreateOrderProductRequest;
import com.ssg.order.api.order.service.request.CreateOrderRequest;
import com.ssg.order.api.order.service.response.OrderCreateResponse;
import com.ssg.order.api.order.service.response.OrderProductCancelResponse;
import com.ssg.order.api.order.service.response.OrderProductsGetResponse;
import com.ssg.order.api.order.service.response.OrderResponse;
import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
import com.ssg.order.domain.common.util.CommonUtil;
import com.ssg.order.domain.domain.order.Order;
import com.ssg.order.domain.domain.order.OrderProduct;
import com.ssg.order.domain.domain.order.usecase.OrderUseCase;
import com.ssg.order.domain.domain.product.Product;
import com.ssg.order.domain.domain.product.usecase.ProductUseCase;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * 주문 도메인과 관련된 함수들이 정의됩니다.
 */
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderUseCase orderUseCase;
    private final ProductUseCase productUseCase;
    private final OrderDtoMapper orderDtoMapper;

    /**
     * 요청받은 상품 목록을 기반으로 재고를 차감한 뒤 주문을 생성한다.
     *
     * @param userId  사용자 ID
     * @param request 주문 생성 Request
     * @return 주문 생성된 주문의 정보
     */
    @Transactional
    public OrderCreateResponse createOrder(Long userId, CreateOrderRequest request) {
        List<CreateOrderProductRequest> orderProductRequests = request.getOrderProductLists();

        List<Long> productIds = CommonUtil.extractIds(
            orderProductRequests,
            CreateOrderProductRequest::getProductId
        );

        List<Product> products = productUseCase.findProductsByProductIds(productIds);

        Map<Long, Product> productMap = CommonUtil.convertToMap(
            products,
            Product::getId
        );

        for (CreateOrderProductRequest orderProduct : orderProductRequests) {
            Product product = productMap.get(orderProduct.getProductId());
            if (ObjectUtils.isEmpty(product)) {
                throw new BusinessException(BusinessErrorCode.NOT_FOUND_PRODUCT,
                                            "productId: " + orderProduct.getProductId());
            }

            // 상품 재고 유효성 검사
            validateProductStock(
                product.getId(),
                product.getProductName(),
                product.getStock(),
                orderProduct.getQuantity()
            );

            // 상품 재고 차감
            productUseCase.updateProductStock(product.getId(), orderProduct.getQuantity(), false);

            // 주문 상품 가격 정보 세팅
            orderProduct.setSellingPrice(product.getSellingPrice() * orderProduct.getQuantity());
            orderProduct.setDiscountAmount(product.getDiscountAmount() * orderProduct.getQuantity());
            orderProduct.setPaymentPrice(
                orderProduct.getSellingPrice() - orderProduct.getDiscountAmount()
            );
        }

        List<OrderProduct> orderProducts = request.getOrderProductLists().
                                                  stream().map(orderDtoMapper::toOrderProduct).toList();

        // 주문 생성
        Order order = orderUseCase.createOrder(userId, orderProducts);

        return orderDtoMapper.toCreateOrderResponse(order);
    }

    /**
     * 주문의 상품 목록를 리턴한다
     *
     * @param orderId 주문 ID
     * @param userId  사용자 ID
     * @return 주문 금액과 주문 상품 목록
     */
    @Transactional(readOnly = true)
    public OrderProductsGetResponse getOrderWithOrderProducts(Long orderId, Long userId) {
        // 주문 상품 조회
        Order order = orderUseCase.getOrderWithOrderProducts(orderId, userId);

        List<OrderProduct> orderProducts = order.getOrderProducts();

        List<Long> productIds = orderProducts
            .stream()
            .map(OrderProduct::getProductId)
            .toList();

        List<Product> products = productUseCase.findProductsByProductIds(productIds);

        Map<Long, Product> productMap = CommonUtil.convertToMap(
            products,
            Product::getId
        );

        // 주문 상품에 상품 정보를 매핑
        List<OrderProductWithProduct> orderProductWithProducts = orderProducts.stream()
            .map(orderProduct -> {
                Product product = productMap.get(orderProduct.getProductId());
                if (ObjectUtils.isEmpty(product)) {
                    throw new BusinessException(BusinessErrorCode.NOT_FOUND_PRODUCT,
                                                "productId: " + orderProduct.getProductId());
                }
                return OrderProductWithProduct.of(orderProduct, product);
            })
            .toList();

        // 주문 상품 응답 변환
        return orderDtoMapper.toOrderProductsGetResponse(order, orderProductWithProducts);
    }

    /**
     * 주문 목록을 리턴한다.
     *
     * @param userId 사용자 ID
     * @return 주문의 전체 정보를 포함한 목록
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(Long userId) {
        List<Order> orders = orderUseCase.findOrdersByUserId(userId, true);
        return orders.stream()
            .map(orderDtoMapper::toOrderResponse)
            .toList();
    }

    /**
     * 주문 상품을 취소하고, 주문 금액을 차감한 후, 취소된 주문 상품의 정보를 포함한 응답을 리턴한다.
     *
     * @param orderId        주문 ID
     * @param orderProductId 주문 상품 ID
     * @param userId         사용자 ID
     * @return 취소된 주문 상품 정보와 주문 금액 정보
     */
    @Transactional
    public OrderProductCancelResponse cancelOrderProduct(
        Long orderId,
        Long orderProductId,
        Long userId
    ) {
        // 주문상품 상태 변경 및 주문 금액 차감
        OrderProduct canceledOrderProduct = orderUseCase.cancelOrderProduct(orderId, orderProductId, userId);

        // 주문 금액 변경
        Order updatedOrder = orderUseCase.subtractOrderPrice(
            orderId,
            userId,
            canceledOrderProduct.getPaymentPrice(),
            canceledOrderProduct.getSellingPrice(),
            canceledOrderProduct.getDiscountAmount()
        );

        // 상품 재고 원복
        productUseCase.updateProductStock(canceledOrderProduct.getProductId(), canceledOrderProduct.getQuantity(), true);

        OrderProductWithProduct orderProductWithProduct = OrderProductWithProduct.of(
            canceledOrderProduct,
            productUseCase.getProductById(canceledOrderProduct.getProductId())
        );

        return orderDtoMapper.toOrderProductCancelResponse(updatedOrder, orderProductWithProduct);
    }

    private void validateProductStock(
        Long productId,
        String productName,
        Integer productStock,
        Integer orderQuantity
    ) {
        if (productStock < orderQuantity) {
            throw new BusinessException(BusinessErrorCode.OUT_OF_STOCK,
                                        "productId: " + productId + ", productName: " + productName);
        }
    }
}
