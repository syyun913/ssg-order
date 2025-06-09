package com.ssg.order.infrastructure.persistence.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
import com.ssg.order.domain.domain.product.Product;
import com.ssg.order.infrastructure.persistence.product.entity.ProductEntity;
import com.ssg.order.infrastructure.persistence.product.mapper.ProductPersistenceMapper;
import com.ssg.order.infrastructure.persistence.product.repository.ProductJpaRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("상품 레포지토리 테스트")
@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {

    @InjectMocks
    private ProductRepository productRepository;

    @Mock
    private ProductPersistenceMapper mapper;

    @Mock
    private ProductJpaRepository productJpaRepository;

    private Product product1;
    private Product product2;
    private ProductEntity productEntity1;
    private ProductEntity productEntity2;

    @BeforeEach
    void setUp() {
        product1 = createProduct(1L, "Product1", 1000, 100, 10);
        product2 = createProduct(2L, "Product2", 2000, 200, 20);

        productEntity1 = createProductEntity(1L, "Product1", 1000, 100, 10);
        productEntity2 = createProductEntity(2L, "Product2", 2000, 200, 20);
    }
    
    @DisplayName("전체 상품 조회")
    @Nested
    class FindAllProductsTests {
        @Test
        @DisplayName("전체 상품 조회 시 상품 리스트가 리턴된다")
        void findAllProducts_Success() {
            // given
            List<ProductEntity> productEntities = Arrays.asList(productEntity1, productEntity2);
            when(productJpaRepository.findAll()).thenReturn(productEntities);
            when(mapper.toDomain(productEntity1)).thenReturn(product1);
            when(mapper.toDomain(productEntity2)).thenReturn(product2);

            // when
            List<Product> result = productRepository.findAllProducts();

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(1).getId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("전체 상품 조회 시상 품이 존재하지 않으면 빈 리스트를 반환한다")
        void findAllProducts_EmptyList() {
            // given
            when(productJpaRepository.findAll()).thenReturn(Arrays.asList());

            // when
            List<Product> result = productRepository.findAllProducts();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }
    }

    @DisplayName("상품 ID로 상품 조회")
    @Nested
    class FindProductsByProductIds {
        @Test
        @DisplayName("상품 ID로 상품 조회 시 상품 리스트가 리턴된다")
        void findProductsByProductIds_Success() {
            // given
            List<Long> productIds = Arrays.asList(1L, 2L);
            List<ProductEntity> productEntities = Arrays.asList(productEntity1, productEntity2);
            when(productJpaRepository.findByIdIn(productIds)).thenReturn(productEntities);
            when(mapper.toDomain(productEntity1)).thenReturn(product1);
            when(mapper.toDomain(productEntity2)).thenReturn(product2);

            // when
            List<Product> result = productRepository.findProductsByProductIds(productIds);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(1).getId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("상품 ID로 상품 조회 시 상품 정보가 조회되지 않으면 빈 리스트가 리턴된다")
        void findProductsByProductIds_EmptyList() {
            // given
            List<Long> productIds = Arrays.asList(999L, 1000L);
            when(productJpaRepository.findByIdIn(productIds)).thenReturn(Arrays.asList());

            // when
            List<Product> result = productRepository.findProductsByProductIds(productIds);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }
    }

    @DisplayName("상품 재고 수정")
    @Nested
    class UpdateProductStockTests {

        @Test
        @DisplayName("상품 재고 수정 시 재고가 수정된다")
        void updateProductStock_Success() {
            // given
            ProductEntity mockProductEntity = mock(ProductEntity.class);
            when(productJpaRepository.findById(1L)).thenReturn(Optional.of(mockProductEntity));

            // when
            productRepository.updateProductStock(1L, 5, false);

            // then
            verify(mockProductEntity).decreaseStock(5);
        }

        @Test
        @DisplayName("상품 재고 수정 시 존재하지 않는 상품이 요청되면 BusinessException이 리턴된다")
        void updateProductStock_NotFoundProduct_ThrowsException() {
            // given
            when(productJpaRepository.findById(999L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productRepository.updateProductStock(999L, 5, false))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertAll(
                            () -> assertThat(businessException.getErrorCode()).isEqualTo(BusinessErrorCode.NOT_FOUND_PRODUCT),
                            () -> assertThat(businessException.getMessage()).isEqualTo("상품을 조회할 수 없습니다.")
                        );
                    });
        }
    }

    @DisplayName("상품 ID로 단일 상품 조회")
    @Nested
    class GetProductByIdTests {
        @Test
        @DisplayName("상품 ID로 단일 상품 조회 시 상품이 리턴된다")
        void getProductById_Success() {
            // given
            when(productJpaRepository.findById(1L)).thenReturn(Optional.of(productEntity1));
            when(mapper.toDomain(productEntity1)).thenReturn(product1);

            // when
            Product result = productRepository.getProductById(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getProductName()).isEqualTo("Product1");
            assertThat(result.getSellingPrice()).isEqualTo(1000);
            assertThat(result.getDiscountAmount()).isEqualTo(100);
            assertThat(result.getStock()).isEqualTo(10);
        }

        @Test
        @DisplayName("상품 ID로 단일 상품 조회 시 존재하지 않는 상품이 요청되면 BusinessException이 리턴된다")
        void getProductById_NotFoundProduct_ThrowsException() {
            // given
            when(productJpaRepository.findById(999L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productRepository.getProductById(999L))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {
                        BusinessException businessException = (BusinessException) exception;
                        assertAll(
                            () -> assertThat(businessException.getErrorCode()).isEqualTo(BusinessErrorCode.NOT_FOUND_PRODUCT),
                            () -> assertThat(businessException.getMessage()).isEqualTo("상품을 조회할 수 없습니다.")
                        );
                    });
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

    private ProductEntity createProductEntity(Long id, String productName, int sellingPrice, int discountAmount, int stock) {
        return ProductEntity.builder()
                .id(id)
                .productName(productName)
                .sellingPrice(sellingPrice)
                .discountAmount(discountAmount)
                .stock(stock)
                .build();
    }
} 