package com.example.budzets.controller;

import com.example.budzets.dto.CategoryProductsDTO;
import com.example.budzets.repository.CheckProductRepository;
import com.example.budzets.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class StatsController {

    private final CheckProductRepository checkProductRepository;
    private final StatsService statsService;

    @GetMapping("/categories")
    public List<Map<String, Object>> getTotalPerCategory() {
        return checkProductRepository.getTotalSpentPerCategory();
    }

    @PostMapping("/categories/filter")
    public Map<String, Object> getSelectedCategoryStats(
            @RequestBody List<String> categories,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        List<Map<String, Object>> filtered = statsService.getTotalSpentPerSelectedCategories(categories, start, end);
        Double total = statsService.getTotalForSelectedCategories(categories, start, end);

        return Map.of("categories", filtered, "total", total);
    }

    @GetMapping("/categories/products/filter")
    public List<CategoryProductsDTO> getProductsByCategoryAndDate(
            @RequestParam(value = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,

            @RequestParam(value = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return (start != null && end != null)
                ? statsService.getProductsGroupedByCategoryAndDateRange(start, end)
                : statsService.getProductsGroupedByCategoryAllTime();
    }
}
