package com.example.budzets.service;

import com.example.budzets.dto.CategoryProductsDTO;
import com.example.budzets.dto.ProductWithTotalDTO;
import com.example.budzets.model.CategoryEntity;
import com.example.budzets.model.CheckProductEntity;
import com.example.budzets.model.ProductEntity;
import com.example.budzets.repository.CheckProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatsServiceTest {

    private CheckProductRepository checkProductRepository;
    private ReceiptService receiptService;
    private StatsService statsService;

    @BeforeEach
    void setUp() {
        checkProductRepository = mock(CheckProductRepository.class);
        receiptService = mock(ReceiptService.class);
        statsService = new StatsService(checkProductRepository, receiptService);
    }

    @Test
    void testGetProductsGroupedByCategoryAndDateRange() {
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();

        ProductEntity product = new ProductEntity();
        product.setName("Piens");
        product.setUnitPrice(1.0);
        CategoryEntity category = new CategoryEntity();
        category.setName("Pārtika");
        product.setCategory(category);

        CheckProductEntity cp = CheckProductEntity.builder()
                .product(product)
                .quantity(2)
                .totalPrice(2.0)
                .discountAmount(0.5)
                .build();

        when(receiptService.getCheckProductsWithCategoryByDateRange(start, end))
                .thenReturn(List.of(cp));

        List<CategoryProductsDTO> result = statsService.getProductsGroupedByCategoryAndDateRange(start, end);

        assertEquals(1, result.size());
        assertEquals("Pārtika", result.get(0).getCategory());
        assertEquals(1, result.get(0).getProducts().size());
        assertEquals("Piens", result.get(0).getProducts().get(0).getName());
    }

    @Test
    void testGetTotalSpentPerSelectedCategoriesWithDate() {
        List<String> categories = List.of("Pārtika");
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();

        Map<String, Object> mockResult = Map.of("category", "Pārtika", "total", 20.0);

        when(checkProductRepository.getTotalSpentPerSelectedCategories(categories, start, end))
                .thenReturn(List.of(mockResult));

        List<Map<String, Object>> result = statsService.getTotalSpentPerSelectedCategories(categories, start, end);
        assertEquals(1, result.size());
        assertEquals("Pārtika", result.get(0).get("category"));
    }

    @Test
    void testGetTotalForSelectedCategoriesWithDate() {
        List<String> categories = List.of("Dzērieni");
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now();

        when(checkProductRepository.getTotalForSelectedCategories(categories, start, end))
                .thenReturn(15.0);

        Double result = statsService.getTotalForSelectedCategories(categories, start, end);
        assertEquals(15.0, result);
    }

    @Test
    void testGetProductsByCategoryAndDate() {
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();
        String category = "Augļi";

        ProductEntity product = new ProductEntity();
        product.setName("Banāni");
        product.setUnitPrice(1.2);

        CheckProductEntity cp = CheckProductEntity.builder()
                .product(product)
                .quantity(3)
                .totalPrice(3.6)
                .discountAmount(0.0)
                .build();

        when(checkProductRepository.findByCategoryAndDate(category, start, end))
                .thenReturn(List.of(cp));

        List<ProductWithTotalDTO> result = statsService.getProductsByCategoryAndDate(category, start, end);

        assertEquals(1, result.size());
        assertEquals("Banāni", result.get(0).getName());
        assertEquals(3.6, result.get(0).getTotal());
    }
}
