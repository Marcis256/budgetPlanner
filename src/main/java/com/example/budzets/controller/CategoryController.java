package com.example.budzets.controller;

import com.example.budzets.dto.ProductWithTotalDTO;
import com.example.budzets.repository.CategoryRepository;
import com.example.budzets.model.CategoryEntity;
import com.example.budzets.service.StatsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:3000")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final StatsService statsService;

    public CategoryController(CategoryRepository categoryRepository, StatsService statsService) {
        this.categoryRepository = categoryRepository;
        this.statsService = statsService;
    }

    @PostMapping
    public CategoryEntity createCategory(@RequestBody CategoryEntity category) {
        return categoryRepository.save(category);
    }

    @GetMapping
    public List<CategoryEntity> getAll() {
        return categoryRepository.findAll();
    }

    @GetMapping("/products")
    public List<ProductWithTotalDTO> getProductsByCategoryAndDate(
            @RequestParam("category") String category,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return statsService.getProductsByCategoryAndDate(category, start, end);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategorija nav atrasta"));

        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Nevar dzēst kategoriju, kurai pievienoti produkti.");
        }

        categoryRepository.deleteById(id);
        return ResponseEntity.ok("Kategorija veiksmīgi dzēsta.");
    }

    @PutMapping("/{id}")
    public CategoryEntity updateCategory(@PathVariable Long id, @RequestBody CategoryEntity updatedCategory) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setName(updatedCategory.getName());
                    return categoryRepository.save(category);
                })
                .orElseThrow(() -> new IllegalArgumentException("Kategorija nav atrasta ar ID: " + id));
    }

}