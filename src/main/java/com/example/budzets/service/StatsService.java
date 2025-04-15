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
    private final ReceiptRepository receiptRepository;

    public StatsService(CheckProductRepository checkProductRepository, ReceiptRepository receiptRepository) {
        this.checkProductRepository = checkProductRepository;
        this.receiptRepository = receiptRepository;
    }

    public List<CategoryProductsDTO> getProductsGroupedByCategoryAndDateRange(LocalDateTime start, LocalDateTime end) {
        List<CheckProductEntity> all = receiptRepository.findByDateBetween(start, end, Sort.by(Sort.Direction.DESC, "date"))
                .stream()
                .flatMap(r -> r.getProducts().stream())
                .collect(Collectors.toList());

        // Grupējam pēc kategorijas
        Map<String, List<CheckProductEntity>> byCategory = all.stream()
                .filter(p -> p.getProduct().getCategory() != null)
                .collect(Collectors.groupingBy(p -> p.getProduct().getCategory().getName()));

        List<CategoryProductsDTO> result = new ArrayList<>();

        for (String category : byCategory.keySet()) {
            List<CheckProductEntity> products = byCategory.get(category);

            // Grupējam pēc nosaukuma + cenas
            Map<String, ProductWithTotalDTO> grouped = new HashMap<>();

            for (CheckProductEntity p : products) {
                String key = p.getProduct().getName() + "_" + p.getProduct().getUnitPrice();

                grouped.merge(key,
                        new ProductWithTotalDTO(
                                p.getProduct().getName(),
                                p.getProduct().getUnitPrice(),
                                p.getQuantity(),
                                p.getTotalPrice(),
                                p.getDiscountAmount() != null ? p.getDiscountAmount() : 0.0
                        ),
                        (existing, next) -> new ProductWithTotalDTO(
                                existing.getName(),
                                existing.getUnitPrice(),
                                existing.getQuantity() + next.getQuantity(),
                                existing.getTotal() + next.getTotal(),
                                existing.getDiscountAmount() + next.getDiscountAmount() // ✅ summējam atlaides
                        )
                );
            }

            result.add(new CategoryProductsDTO(category, new ArrayList<>(grouped.values())));
        }

        return result;
    }


    public List<Map<String, Object>> getTotalSpentPerSelectedCategories(List<String> categories, LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return checkProductRepository.getTotalSpentPerSelectedCategoriesWithoutDate(categories);
        }
        return checkProductRepository.getTotalSpentPerSelectedCategories(categories, start, end);
    }

    public Double getTotalForSelectedCategories(List<String> categories, LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return checkProductRepository.getTotalForSelectedCategoriesWithoutDate(categories);
        }
        return checkProductRepository.getTotalForSelectedCategories(categories, start, end);
    }

    public List<ProductWithTotalDTO> getProductsByCategoryAndDate(String category, LocalDateTime start, LocalDateTime end) {
        List<CheckProductEntity> products = checkProductRepository.findByCategoryAndDate(category, start, end);

        return products.stream()
                .map(p -> new ProductWithTotalDTO(
                        p.getProduct().getName(),
                        p.getProduct().getUnitPrice(),
                        p.getQuantity(),
                        p.getTotalPrice(),
                        p.getDiscountAmount() != null ? p.getDiscountAmount() : 0.0
                ))
                .toList();
    }

    public List<CategoryProductsDTO> getProductsGroupedByCategoryAllTime() {
        List<CheckProductEntity> all = checkProductRepository.findAll();

        Map<String, Map<String, ProductWithTotalDTO>> grouped = new HashMap<>();

        for (CheckProductEntity cp : all) {
            ProductEntity p = cp.getProduct();
            String category = p.getCategory() != null ? p.getCategory().getName() : "Bez kategorijas";

            grouped.putIfAbsent(category, new HashMap<>());

            Map<String, ProductWithTotalDTO> productMap = grouped.get(category);
            String key = p.getName() + "_" + p.getUnitPrice();

            productMap.putIfAbsent(key, new ProductWithTotalDTO(
                    p.getName(),
                    p.getUnitPrice(),
                    0,
                    0.0,
                    0.0
            ));

            ProductWithTotalDTO dto = productMap.get(key);
            dto.setQuantity(dto.getQuantity() + cp.getQuantity());
            dto.setTotal(dto.getTotal() + cp.getTotalPrice());
            dto.setDiscountAmount(dto.getDiscountAmount() + (cp.getDiscountAmount() != null ? cp.getDiscountAmount() : 0.0));
        }

        return grouped.entrySet().stream()
                .map(entry -> new CategoryProductsDTO(
                        entry.getKey(),
                        new ArrayList<>(entry.getValue().values())
                ))
                .toList();
    }


}
