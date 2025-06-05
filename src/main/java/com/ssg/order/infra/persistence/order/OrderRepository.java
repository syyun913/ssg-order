package com.ssg.order.infra.persistence.order;

import com.ssg.order.domain.order.Order;
import com.ssg.order.domain.order.repository.OrderWriteRepository;
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
public class OrderRepository implements OrderWriteRepository {
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
}
