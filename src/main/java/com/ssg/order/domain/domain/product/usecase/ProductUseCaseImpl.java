package com.ssg.order.domain.domain.product.usecase;

import com.ssg.order.domain.common.annotation.UseCase;
import com.ssg.order.domain.domain.product.Product;
import com.ssg.order.domain.domain.product.repository.ProductReadRepository;
import com.ssg.order.domain.domain.product.repository.ProductWriteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
class ProductUseCaseImpl implements ProductUseCase {
    private final ProductReadRepository productReadRepository;
    private final ProductWriteRepository productWriteRepository;

    /**
     * 모든 상품을 조회합니다.
     * @return 상품 목록
     */
    @Override
    public List<Product> findAllProducts() {
        return productReadRepository.findAllProducts();
    }

    /**
     * 상품 ID 목록에 해당하는 상품들을 조회합니다.
     * @param productIds 조회할 상품 ID 목록
     * @return 상품 목록
     */
    @Override
    public List<Product> findProductsByProductIds(List<Long> productIds) {
        return productReadRepository.findProductsByProductIds(productIds);
    }

    /**
     * 상품의 재고를 업데이트합니다.
     * @param productId 상품 ID
     * @param updateQuantity 업데이트할 수량
     * @param isIncrease 재고 증가 여부 (true: 증가, false: 감소)
     */
    @Override
    public void updateProductStock(Long productId, int updateQuantity, boolean isIncrease) {
        productWriteRepository.updateProductStock(productId, updateQuantity, isIncrease);
    }

    /**
     * 상품 ID로 상품을 조회합니다.
     * @param productId 상품 ID
     * @return 저장된 상품 정보
     */
    @Override
    public Product getProductById(Long productId) {
        return productReadRepository.getProductById(productId);
    }
}
