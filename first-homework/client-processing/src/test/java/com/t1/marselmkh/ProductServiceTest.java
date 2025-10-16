package com.t1.marselmkh;

import com.t1.marselmkh.dto.ProductDto.ProductCreateDto;
import com.t1.marselmkh.dto.ProductDto.ProductUpdateDto;
import com.t1.marselmkh.dto.ProductDto.ProductViewDto;
import com.t1.marselmkh.entity.Product;
import com.t1.marselmkh.entity.ProductKey;
import com.t1.marselmkh.exception.ProductNotFoundException;
import com.t1.marselmkh.mapper.ProductMapper;
import com.t1.marselmkh.repository.ProductRepository;
import com.t1.marselmkh.service.ProductService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    private Product product;
    private ProductCreateDto createDto;
    private ProductUpdateDto updateDto;
    private ProductViewDto viewDto;

    private static final Long PRODUCT_ID = 1L;
    private static final String PRODUCT_NAME = "Test Product";

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(PRODUCT_ID);
        product.setName(PRODUCT_NAME);
        product.setProductId("PID-001");
        product.setKey(ProductKey.DC);

        createDto = new ProductCreateDto();
        createDto.setName(PRODUCT_NAME);
        createDto.setKey(ProductKey.DC);

        updateDto = new ProductUpdateDto();
        updateDto.setName("Updated Product");

        viewDto = new ProductViewDto();
        viewDto.setName(PRODUCT_NAME);
    }

    @Test
    void givenValidProductDto_whenCreateProduct_thenProductSavedAndReturned() {
        when(productMapper.toEntity(createDto)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(viewDto);

        ProductViewDto result = productService.createProduct(createDto);

        verify(productRepository).save(product);
        verify(productMapper).toEntity(createDto);
        verify(productMapper).toDto(product);
        assertThat(result).isEqualTo(viewDto);
        assertThat(product.getCreateDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void givenExistingProduct_whenUpdateProduct_thenUpdatedAndReturned() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(viewDto);

        ProductViewDto result = productService.updateProduct(PRODUCT_ID, updateDto);

        verify(productRepository).save(product);
        assertThat(product.getName()).isEqualTo(updateDto.getName());
        assertThat(result).isEqualTo(viewDto);
    }

    @Test
    void givenNonExistingProduct_whenUpdateProduct_thenThrowsException() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(PRODUCT_ID, updateDto))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found");

        verify(productRepository, never()).save(any());
    }

    @Test
    void givenExistingProductId_whenGetProduct_thenReturnViewDto() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(viewDto);

        ProductViewDto result = productService.getProduct(PRODUCT_ID);

        verify(productRepository).findById(PRODUCT_ID);
        assertThat(result).isEqualTo(viewDto);
    }

    @Test
    void givenNonExistingProductId_whenGetProduct_thenThrowsException() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct(PRODUCT_ID))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found");
    }

    @Test
    void givenExistingProduct_whenDeleteProduct_thenDeletedSuccessfully() {
        when(productRepository.existsById(PRODUCT_ID)).thenReturn(true);

        productService.deleteProduct(PRODUCT_ID);

        verify(productRepository).deleteById(PRODUCT_ID);
    }

    @Test
    void givenNonExistingProduct_whenDeleteProduct_thenThrowsException() {
        when(productRepository.existsById(PRODUCT_ID)).thenReturn(false);

        assertThatThrownBy(() -> productService.deleteProduct(PRODUCT_ID))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found");

        verify(productRepository, never()).deleteById(any());
    }
}
