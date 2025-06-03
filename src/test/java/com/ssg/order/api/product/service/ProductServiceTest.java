package com.ssg.order.api.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ssg.order.domain.product.Product;
import com.ssg.order.domain.product.usecase.ProductUseCase;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductUseCase productUseCase;

    @Test
    void findAllProducts_shouldReturnProductList() {
        // given
        Product product1 = createProduct(1L, "Product1", 1000, 100, 10);
        Product product2 = createProduct(2L, "Product2", 2000, 200, 20);
        List<Product> mockProducts = Arrays.asList(product1, product2);
        when(productUseCase.findAllProducts()).thenReturn(mockProducts);

        // when
        List<Product> result = productService.findAllProducts();

        // then
        assertThat(result).isEqualTo(mockProducts);
        verify(productUseCase, times(1)).findAllProducts();
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