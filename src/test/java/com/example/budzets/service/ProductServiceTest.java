package com.example.budzets.service;

import com.example.budzets.dto.ProductCategoryUpdateDTO;
import com.example.budzets.model.CategoryEntity;
import com.example.budzets.model.ProductEntity;
import com.example.budzets.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository productRepository;
    private CategoryService categoryService;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        categoryService = mock(CategoryService.class);
        productService = new ProductService(productRepository, categoryService);
    }

    @Test
    void testUpdateCategory() {
        Long productId = 1L;
        Long categoryId = 2L;

        ProductEntity product = new ProductEntity();
        CategoryEntity category = new CategoryEntity();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryService.findByIdOrThrow(categoryId)).thenReturn(category);

        productService.updateCategory(productId, categoryId);

        assertEquals(category, product.getCategory());
        verify(productRepository).save(product);
    }

    @Test
    void testFindOrCreateProductByNameAndPrice_WhenExists() {
        String name = "Milk";
        double price = 1.234;
        double roundedPrice = 1.23;

        ProductEntity existingProduct = new ProductEntity();
        when(productRepository.findByNameAndUnitPrice(name, roundedPrice))
                .thenReturn(Optional.of(existingProduct));

        ProductEntity result = productService.findOrCreateProductByNameAndPrice(name, price);

        assertEquals(existingProduct, result);
        verify(productRepository, never()).save(any());
    }

    @Test
    void testFindOrCreateProductByNameAndPrice_WhenNotExists() {
        String name = "Bread";
        double price = 0.987;
        double roundedPrice = 0.99;

        when(productRepository.findByNameAndUnitPrice(name, roundedPrice))
                .thenReturn(Optional.empty());

        ArgumentCaptor<ProductEntity> captor = ArgumentCaptor.forClass(ProductEntity.class);
        when(productRepository.save(any(ProductEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        ProductEntity result = productService.findOrCreateProductByNameAndPrice(name, price);

        assertEquals(name, result.getName());
        assertEquals(roundedPrice, result.getUnitPrice());
        verify(productRepository).save(captor.capture());
    }

    @Test
    void testFindByIdOrThrow_WhenNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> productService.findByIdOrThrow(1L));
    }

    @Test
    void testCreateNewProduct() {
        String name = "Eggs";
        double price = 2.449;
        double rounded = 2.45;

        when(productRepository.save(any(ProductEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        ProductEntity result = productService.createNewProduct(name, price);

        assertEquals(name, result.getName());
        assertEquals(rounded, result.getUnitPrice());
    }

    @Test
    void testUpdateCategories() {
        ProductCategoryUpdateDTO dto = new ProductCategoryUpdateDTO();
        dto.setProductId(1L);
        dto.setCategoryId(2L);

        ProductEntity product = new ProductEntity();
        CategoryEntity category = new CategoryEntity();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryService.findByIdOrThrow(2L)).thenReturn(category);

        productService.updateCategories(List.of(dto));

        assertEquals(category, product.getCategory());
        verify(productRepository).save(product);
    }
}
