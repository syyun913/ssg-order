package com.ssg.order.infra.persistence.order.repository;

import com.ssg.order.infra.persistence.order.entity.OrderProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductJpaRepository extends JpaRepository<OrderProductEntity, Long> {

}
