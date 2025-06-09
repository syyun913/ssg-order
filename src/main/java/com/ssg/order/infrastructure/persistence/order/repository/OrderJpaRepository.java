package com.ssg.order.infrastructure.persistence.order.repository;

import com.ssg.order.infrastructure.persistence.order.entity.OrderEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByIdAndUserId(Long orderId, Long userId);
    List<OrderEntity> findAllByUserId(Long userId);
    List<OrderEntity> findAllByUserIdOrderByIdDesc(Long userId);
    List<OrderEntity> findAllByUserIdOrderByIdAsc(Long userId);
}
