package com.ssg.order.infra.persistence.product;

import com.ssg.order.domain.product.Product;
import com.ssg.order.domain.product.repository.ProductReadRepository;
import com.ssg.order.infra.persistence.product.entity.ProductEntity;
import com.ssg.order.infra.persistence.product.mapper.ProductPersistenceMapper;
import com.ssg.order.infra.persistence.product.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductRepository implements ProductReadRepository {
    private final ProductPersistenceMapper mapper;
    private final ProductJpaRepository productJpaRepository;

    @Override
    public List<Product> findAllProducts() {
        List<ProductEntity> productEntities = productJpaRepository.findAll();

        return productEntities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
} 