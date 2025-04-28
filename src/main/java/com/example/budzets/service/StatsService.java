package com.example.budzets.service;

import com.example.budzets.dto.CategoryProductsDTO;
import com.example.budzets.dto.ProductWithTotalDTO;
import com.example.budzets.model.CheckProductEntity;
import com.example.budzets.model.ProductEntity;
import com.example.budzets.repository.CheckProductRepository;
import com.example.budzets.repository.ReceiptRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final CheckProductRepository checkProductRepository;
    private final ReceiptService receiptService;

    public StatsService(CheckProductRepository checkProductRepository, ReceiptService receiptService) {
        this.checkProductRepository = checkProductRepository;
        this.receiptService = receiptService;
    }

    public List<CategoryProductsDTO> getProductsGroupedByCategoryAndDateRange(LocalDateTime start, LocalDateTime end) {
        List<CheckProductEntity> products = receiptService.getCheckProductsWithCategoryByDateRange(start, end);
        return groupProductsByCategory(products);
    }

    public List<CategoryProductsDTO> getProductsGroupedByCategoryAllTime() {
        List<CheckProductEntity> products = checkProductRepository.findAll();
        return groupProductsByCategory(products);
    }

    public List<Map<String, Object>> getTotalSpentPerSelectedCategories(List<String> categories, LocalDateTime start, LocalDateTime end) {
        return (start == null || end == null)
                ? checkProductRepository.getTotalSpentPerSelectedCategoriesWithoutDate(categories)
                : checkProductRepository.getTotalSpentPerSelectedCategories(categories, start, end);
    }

    public Double getTotalForSelectedCategories(List<String> categories, LocalDateTime start, LocalDateTime end) {
        return (start == null || end == null)
                ? checkProductRepository.getTotalForSelectedCategoriesWithoutDate(categories)
                : checkProductRepository.getTotalForSelectedCategories(categories, start, end);
    }

    public List<ProductWithTotalDTO> getProductsByCategoryAndDate(String category, LocalDateTime start, LocalDateTime end) {
        return checkProductRepository.findByCategoryAndDate(category, start, end)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // === 👇 Palīgmetodes 👇 ===

    private List<CategoryProductsDTO> groupProductsByCategory(List<CheckProductEntity> products) {
        Map<String, Map<String, ProductWithTotalDTO>> groupedByCategory = new HashMap<>();

        for (CheckProductEntity cp : products) {
            ProductEntity p = cp.getProduct();
            String category = (p.getCategory() != null) ? p.getCategory().getName() : "Bez kategorijas";
            String key = p.getName() + "_" + p.getUnitPrice();

            groupedByCategory
                    .computeIfAbsent(category, c -> new HashMap<>())
                    .merge(key, toDTO(cp), this::mergeProductData);
        }

        return groupedByCategory.entrySet().stream()
                .map(entry -> new CategoryProductsDTO(entry.getKey(), new ArrayList<>(entry.getValue().values())))
                .collect(Collectors.toList());
        }

    private ProductWithTotalDTO toDTO(CheckProductEntity cp) {
        return new ProductWithTotalDTO(
                cp.getProduct().getName(),
                cp.getProduct().getUnitPrice(),
                cp.getQuantity(),
                cp.getTotalPrice(),
                cp.getDiscountAmount() != null ? cp.getDiscountAmount() : 0.0
        );
    }

    private ProductWithTotalDTO mergeProductData(ProductWithTotalDTO a, ProductWithTotalDTO b) {
        return new ProductWithTotalDTO(
                a.getName(),
                a.getUnitPrice(),
                a.getQuantity() + b.getQuantity(),
                a.getTotal() + b.getTotal(),
                a.getDiscountAmount() + b.getDiscountAmount()
        );
    }
}
