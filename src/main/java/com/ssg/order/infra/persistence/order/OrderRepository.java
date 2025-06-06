package com.ssg.order.infra.persistence.order;

import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
import com.ssg.order.domain.domain.order.Order;
import com.ssg.order.domain.domain.order.repository.OrderReadRepository;
import com.ssg.order.domain.domain.order.repository.OrderWriteRepository;
import com.ssg.order.infra.persistence.order.entity.OrderEntity;
import com.ssg.order.infra.persistence.order.entity.OrderProductEntity;
import com.ssg.order.infra.persistence.order.mapper.OrderPersistenceMapper;
import com.ssg.order.infra.persistence.order.repository.OrderJpaRepository;
import com.ssg.order.infra.persistence.order.repository.OrderProductJpaRepository;
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
        OrderEntity orderEntity = orderJpaRepository.findByIdAndUserId(orderId, userId)
            .orElseThrow(() -> new BusinessException(
                BusinessErrorCode.NOT_FOUND_ORDER,
                "orderId: " + orderId
            ));

        List<OrderProductEntity> productEntities = orderProductJpaRepository.findAllByOrder_Id(orderId);
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
}
