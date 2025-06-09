package com.ssg.order.infrastructure.persistence.product;

import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
import com.ssg.order.domain.domain.product.Product;
import com.ssg.order.domain.domain.product.repository.ProductReadRepository;
import com.ssg.order.domain.domain.product.repository.ProductWriteRepository;
import com.ssg.order.infrastructure.persistence.product.entity.ProductEntity;
import com.ssg.order.infrastructure.persistence.product.mapper.ProductPersistenceMapper;
import com.ssg.order.infrastructure.persistence.product.repository.ProductJpaRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepository implements ProductReadRepository, ProductWriteRepository {
    private final ProductPersistenceMapper mapper;
    private final ProductJpaRepository productJpaRepository;

    @Override
    public List<Product> findAllProducts() {
        List<ProductEntity> productEntities = productJpaRepository.findAll();

        return productEntities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findProductsByProductIds(List<Long> productIds) {
        List<ProductEntity> productEntities = productJpaRepository.findByIdIn(productIds);

        return productEntities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Product getProductById(Long productId) {
        return productJpaRepository.findById(productId)
                .map(mapper::toDomain)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.NOT_FOUND_PRODUCT,
                                                               "productId: " + productId));
    }

    @Override
    public void updateProductStock(Long productId, int updateQuantity, boolean isIncrease) {
        ProductEntity productEntity = productJpaRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.NOT_FOUND_PRODUCT,
                                                               "productId: " + productId));

        if (isIncrease) {
            productEntity.increaseStock(updateQuantity);
        } else {
            productEntity.decreaseStock(updateQuantity);
        }
    }

}