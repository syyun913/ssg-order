package com.ssg.order.infra.persistence.order.repository;

import com.ssg.order.infra.persistence.order.entity.OrderProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductJpaRepository extends JpaRepository<OrderProductEntity, Long> {
    List<OrderProductEntity> findAllByOrder_Id(Long orderId);
}
