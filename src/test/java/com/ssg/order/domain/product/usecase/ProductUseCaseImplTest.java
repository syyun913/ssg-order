package com.ssg.order.domain.product.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.ssg.order.domain.product.Product;
import com.ssg.order.domain.product.repository.ProductReadRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseImplTest {
    @InjectMocks
    private ProductUseCaseImpl productUseCase;

    @Mock
    private ProductReadRepository productReadRepository;

    @Test
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