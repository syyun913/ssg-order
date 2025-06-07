package com.ssg.order.domain.domain.product.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.ssg.order.domain.domain.product.Product;
import com.ssg.order.domain.domain.product.repository.ProductReadRepository;
import com.ssg.order.domain.domain.product.repository.ProductWriteRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("상품 유즈케이스 테스트")
@ExtendWith(MockitoExtension.class)
class ProductUseCaseImplTest {
    @InjectMocks
    private ProductUseCaseImpl productUseCase;

    @Mock
    private ProductReadRepository productReadRepository;

    @Mock
    private ProductWriteRepository productWriteRepository;

    @Nested
    @DisplayName("전체 상품 목록 조회")
    class FindAllProducts {
        @Test
        @DisplayName("전체 목록 조회 요청 시 모든 상품 목록이 리턴된다")
        void findAllProducts_ShouldReturnAllProducts() {
            // given
            Product product1 = createProduct(1L, "Product1", 1000, 100, 10);
            Product product2 = createProduct(2L, "Product2", 2000, 200, 20);
            List<Product> expectedProducts = Arrays.asList(product1,product2);
            when(productReadRepository.findAllProducts()).thenReturn(expectedProducts);

            // when
            List<Product> actualProducts = productUseCase.findAllProducts();

            // then
            assertThat(actualProducts).isEqualTo(expectedProducts);
            assertThat(actualProducts).hasSize(2);
        }

        @Test
        @DisplayName("전체 목록 조회 요청 시 상품 데이터가 존재하지 않으면 빈 리스트가 리턴된다")
        void findAllProducts_ShouldReturnEmptyListWhenNoProductsExist() {
            // given
            when(productReadRepository.findAllProducts()).thenReturn(List.of());

            // when
            List<Product> actualProducts = productUseCase.findAllProducts();

            // then
            assertThat(actualProducts).isEmpty();
        }
    }

    @Nested
    @DisplayName("상품 목록 조회")
    class FindProductsByProductIds {
        @Test
        @DisplayName("상품 ID 목록으로 조회 시 해당 상품 목록이 리턴된다")
        void findProductsByProductIds_ShouldReturnMatchingProducts() {
            // given
            Product product1 = createProduct(1L, "Product1", 1000, 100, 10);
            Product product2 = createProduct(2L, "Product2", 2000, 200, 20);
            List<Long> productIds = Arrays.asList(1L, 2L);
            List<Product> expectedProducts = Arrays.asList(product1, product2);
            when(productReadRepository.findProductsByProductIds(productIds)).thenReturn(expectedProducts);

            // when
            List<Product> actualProducts = productUseCase.findProductsByProductIds(productIds);

            // then
            assertThat(actualProducts).isEqualTo(expectedProducts);
            assertThat(actualProducts).hasSize(2);
        }

        @Test
        @DisplayName("상품 ID 목록으로 조회 시 해당하는 상품이 없으면 빈 리스트가 리턴된다")
        void findProductsByProductIds_ShouldReturnEmptyListWhenNoProductsExist() {
            // given
            List<Long> productIds = Arrays.asList(3L, 4L);
            when(productReadRepository.findProductsByProductIds(productIds)).thenReturn(List.of());

            // when
            List<Product> actualProducts = productUseCase.findProductsByProductIds(productIds);

            // then
            assertThat(actualProducts).isEmpty();
        }
    }

    @Nested
    @DisplayName("상품 재고 업데이트")
    class UpdateProductStock {
        @Test
        @DisplayName("상품 재고 업데이트 요청 시 repository의 updateProductStock이 호출된다")
        void updateProductStock_ShouldCallRepository() {
            // given
            Long productId = 1L;
            int decreaseQuantity = 5;

            // when
            productUseCase.updateProductStock(productId, decreaseQuantity, false);

            // then
            org.mockito.Mockito.verify(productWriteRepository)
                .updateProductStock(productId, decreaseQuantity, false);
        }
    }

    private Product createProduct(Long id, String productName, int sellingPrice, int discountAmount, int stock) {
        return Product.builder()
            .id(id)
            .productName(productName)
            .sellingPrice(sellingPrice)
            .discountAmount(discountAmount)
            .stock(stock)
            .build();
    }
}