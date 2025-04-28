package com.example.budzets.controller;

import com.example.budzets.dto.ProductCategoryUpdateDTO;
import com.example.budzets.model.ProductEntity;
import com.example.budzets.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductEntity> getAllProducts() {
        return productService.getAllProducts();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReceipt(@PathVariable Long id) {
        return productService.deleteProducttById(id)
                ? ResponseEntity.ok("Produkts veiksmīgi dzēsts.")
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/by-category/{categoryId}")
    public List<ProductEntity> getProductsByCategory(@PathVariable String categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    @PutMapping("/update-categories")
    public ResponseEntity<String> updateMultipleProductCategories(@RequestBody List<ProductCategoryUpdateDTO> updates) {
        productService.updateCategories(updates);
        return ResponseEntity.ok("✅ Kategorijas atjaunotas.");
    }

    @PutMapping("/{id}/category")
    public ResponseEntity<Void> updateSingleProductCategory(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request
    ) {
        Long categoryId = request.get("categoryId");
        productService.updateCategory(id, categoryId);
        return ResponseEntity.ok().build();
    }
}
