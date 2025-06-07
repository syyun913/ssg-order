package com.ssg.order.infra.persistence.order.repository;

import com.ssg.order.domain.domain.order.enumtype.OrderProductStatusCode;
import com.ssg.order.infra.persistence.order.entity.OrderProductEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductJpaRepository extends JpaRepository<OrderProductEntity, Long> {
    List<OrderProductEntity> findAllByOrderId(Long orderId);
    Optional<OrderProductEntity> findByIdAndOrderId(Long orderProductId, Long orderId);
    boolean existsByOrderIdAndStatusCodeNot(Long orderId, OrderProductStatusCode statusCode);
}
