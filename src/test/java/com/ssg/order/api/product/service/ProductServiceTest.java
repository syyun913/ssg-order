package com.ssg.order.api.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ssg.order.api.product.mapper.ProductDtoMapper;
import com.ssg.order.api.product.service.response.ProductResponse;
import com.ssg.order.domain.domain.product.Product;
import com.ssg.order.domain.domain.product.usecase.ProductUseCase;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
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

    @Mock
    private ProductDtoMapper productDtoMapper;

    @Test
    @DisplayName("상품 목록 조회 요청 시 모든 상품 목록이 ProductResponse로 변환되어 리턴된다")
    void findAllProducts_ShouldReturnAllProductsAsResponse() {
        // given
        Product product1 = createProduct(1L, "Product1", 1000, 100, 10);
        Product product2 = createProduct(2L, "Product2", 2000, 200, 20);
        List<Product> mockProducts = Arrays.asList(product1, product2);
        
        ProductResponse response1 = createProductResponse(1L, "Product1", 1000, 100, 10);
        ProductResponse response2 = createProductResponse(2L, "Product2", 2000, 200, 20);
        List<ProductResponse> expectedResponses = Arrays.asList(response1, response2);

        when(productUseCase.findAllProducts()).thenReturn(mockProducts);
        when(productDtoMapper.toProductResponse(product1)).thenReturn(response1);
        when(productDtoMapper.toProductResponse(product2)).thenReturn(response2);

        // when
        List<ProductResponse> result = productService.findAllProducts();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedResponses);
        verify(productUseCase, times(1)).findAllProducts();
        verify(productDtoMapper, times(1)).toProductResponse(product1);
        verify(productDtoMapper, times(1)).toProductResponse(product2);
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

    private ProductResponse createProductResponse(Long id, String productName, int sellingPrice, int discountAmount, int stock) {
        return ProductResponse.builder()
                .id(id)
                .productName(productName)
                .sellingPrice(sellingPrice)
                .discountAmount(discountAmount)
                .stock(stock)
                .build();
    }
}