package com.ssg.order.infrastructure.persistence.order;

import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
import com.ssg.order.domain.domain.order.Order;
import com.ssg.order.domain.domain.order.OrderProduct;
import com.ssg.order.domain.domain.order.enumtype.OrderProductStatusCode;
import com.ssg.order.domain.domain.order.enumtype.OrderStatusCode;
import com.ssg.order.domain.domain.order.repository.OrderReadRepository;
import com.ssg.order.domain.domain.order.repository.OrderWriteRepository;
import com.ssg.order.infrastructure.persistence.order.entity.OrderEntity;
import com.ssg.order.infrastructure.persistence.order.entity.OrderProductEntity;
import com.ssg.order.infrastructure.persistence.order.mapper.OrderPersistenceMapper;
import com.ssg.order.infrastructure.persistence.order.repository.OrderJpaRepository;
import com.ssg.order.infrastructure.persistence.order.repository.OrderProductJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepository implements OrderWriteRepository, OrderReadRepository {
    private final OrderPersistenceMapper mapper;
    private final OrderJpaRepository orderJpaRepository;
    private final OrderProductJpaRepository orderProductJpaRepository;

    @Override
    public Order saveOrder(Order order) {
        OrderEntity savedOrderEntity = orderJpaRepository.save(mapper.toEntity(order));
        List<OrderProductEntity> orderProductEntities = order.getOrderProducts().stream()
                .map(orderProduct -> mapper.toEntity(orderProduct, savedOrderEntity))
                .toList();
        List<OrderProductEntity> savedOrderProductEntities = orderProductJpaRepository.saveAll(orderProductEntities);

        return mapper.toDomain(savedOrderEntity, savedOrderProductEntities);
    }

    @Override
    public Order getOrderWithOrderProductsById(Long orderId, Long userId) {
        OrderEntity orderEntity = getOrderByIdAndUserId(orderId, userId);

        List<OrderProductEntity> productEntities = orderProductJpaRepository.findAllByOrderId(orderId);
        if (productEntities.isEmpty()) {
            throw new BusinessException(
                BusinessErrorCode.NOT_FOUND_ORDER_PRODUCT,
                "orderId: " + orderId
            );
        }

        return mapper.toDomain(orderEntity, productEntities);
    }

    @Override
    public List<Order> findOrdersByUserId(Long userId, boolean isDescending) {
        List<OrderEntity> orderEntities = isDescending 
            ? orderJpaRepository.findAllByUserIdOrderByIdDesc(userId)
            : orderJpaRepository.findAllByUserIdOrderByIdAsc(userId);

        return orderEntities.stream()
            .map(orderEntity -> mapper.toDomain(orderEntity, null))
            .toList();
    }

    @Override
    public OrderProduct cancelOrderProduct(Long orderId, Long orderProductId, Long userId) {
        OrderEntity orderEntity = getOrderByIdAndUserId(orderId, userId);
        OrderProductEntity orderProductEntity = getOrderProductByIdAndOrderId(orderProductId, orderId);

        // 주문 상품 상태를 CANCELED로 변경
        if (orderProductEntity.getStatusCode() == OrderProductStatusCode.CANCELED) {
            throw new BusinessException(
                BusinessErrorCode.ALREADY_CANCELED_ORDER_PRODUCT,
                "orderProductId: " + orderProductId
            );
        }
        orderProductEntity.updateStatusCode(OrderProductStatusCode.CANCELED);
        orderProductJpaRepository.save(orderProductEntity);

        if (!orderProductJpaRepository.existsByOrderIdAndStatusCodeNot(orderId, OrderProductStatusCode.CANCELED)){
            // 주문 상품이 모두 CANCELED 상태인 경우, 주문 상태를 CANCELED로 변경
            orderEntity.updateStatusCode(OrderStatusCode.CANCELED);
        }

        return mapper.toDomain(orderProductEntity);
    }

    @Override
    public Order subtractOrderPrice(Long orderId,
                                    Long userId,
                                    Integer subtractPaymentPrice,
                                    Integer subtractSellingPrice,
                                    Integer subtractDiscountAmount) {

        OrderEntity orderEntity = getOrderByIdAndUserId(orderId, userId);
        orderEntity.subtractPaymentPrice(subtractPaymentPrice);
        orderEntity.subtractSellingPrice(subtractSellingPrice);
        orderEntity.subtractDiscountAmount(subtractDiscountAmount);

        return mapper.toDomain(orderJpaRepository.save(orderEntity), null);
    }

    private OrderEntity getOrderByIdAndUserId(Long orderId, Long userId) {
        return orderJpaRepository.findByIdAndUserId(orderId, userId)
                                                    .orElseThrow(() -> new BusinessException(
                                                        BusinessErrorCode.NOT_FOUND_ORDER,
                                                        "orderId: " + orderId
                                                    ));
    }

    private OrderProductEntity getOrderProductByIdAndOrderId(Long orderProductId, Long orderId) {
        return orderProductJpaRepository.findByIdAndOrderId(orderProductId, orderId)
                                        .orElseThrow(() -> new BusinessException(
                                            BusinessErrorCode.NOT_FOUND_ORDER_PRODUCT,
                                            "orderProductId: " + orderProductId + ", orderId: " + orderId
                                        ));
    }
}
