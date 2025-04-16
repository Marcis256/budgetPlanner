package com.example.budzets.controller;

import com.example.budzets.dto.CategoryDTO;
import com.example.budzets.model.CategoryEntity;
import com.example.budzets.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:3000") // var aizvietot ar * vai konfigurēt properties
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<CategoryEntity> getAll() {
        return categoryRepository.findAll();
    }

    @PostMapping
    public CategoryEntity create(@Valid @RequestBody CategoryDTO dto) {
        CategoryEntity category = new CategoryEntity();
        category.setName(dto.getName());
        return categoryRepository.save(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CategoryDTO dto) {
        return categoryRepository.findById(id).map(category -> {
            category.setName(dto.getName());
            return ResponseEntity.ok(categoryRepository.save(category));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return categoryRepository.findById(id).map(category -> {
            if (!CollectionUtils.isEmpty(category.getProducts())) {
                return ResponseEntity.badRequest().body("❌ Nevar dzēst kategoriju, kurai piesaistīti produkti.");
            }
            categoryRepository.deleteById(id);
            return ResponseEntity.ok("✅ Kategorija veiksmīgi dzēsta.");
        }).orElse(ResponseEntity.notFound().build());
    }
}
