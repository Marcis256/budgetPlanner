package com.example.budzets.controller;

import com.example.budzets.dto.ProductCategoryUpdateDTO;
import com.example.budzets.repository.CategoryRepository;
import com.example.budzets.repository.ProductRepository;
import com.example.budzets.model.CategoryEntity;
import com.example.budzets.model.ProductEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductController(ProductRepository productRepository,
                             CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<ProductEntity> getAll() {
        return productRepository.findAll();
    }

    @GetMapping("/by-category/{categoryId}")
    public List<ProductEntity> getByCategory(@PathVariable String categoryId) {
        if (categoryId.equals("none")) {
            return productRepository.findByCategoryIsNull();
        } else {
            try {
                Long id = Long.parseLong(categoryId);
                return productRepository.findByCategory_Id(id);
            } catch (NumberFormatException e) {
                return List.of(); // tukšs saraksts, ja ID nav skaitlis
            }
        }
    }

    @PutMapping("/update-categories")
    public ResponseEntity<?> updateMultipleCategories(@RequestBody List<ProductCategoryUpdateDTO> updates) {
        for (ProductCategoryUpdateDTO update : updates) {
            ProductEntity product = productRepository.findById(update.getProductId())
                    .orElseThrow(() -> new RuntimeException("Produkta ID nav atrasts: " + update.getProductId()));

            CategoryEntity category = update.getCategoryId() != null
                    ? categoryRepository.findById(update.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Kategorijas ID nav atrasts: " + update.getCategoryId()))
                    : null;

            product.setCategory(category);
            productRepository.save(product);
        }
        return ResponseEntity.ok("✅ Kategorijas atjaunotas.");
    }

    @PutMapping("/{id}/category")
    public ResponseEntity<?> updateProductCategory(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request
    ) {
        Long categoryId = request.get("categoryId");

        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produkts nav atrasts"));

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Kategorija nav atrasta"));

        product.setCategory(category);
        productRepository.save(product);

        return ResponseEntity.ok().build();
    }
}