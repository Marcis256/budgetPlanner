package com.example.budzets.controller;

import com.example.budzets.dto.CategoryDTO;
import com.example.budzets.model.CategoryEntity;
import com.example.budzets.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:3000")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryEntity> getAll() {
        return categoryService.getAllCategories();
    }

    @PostMapping
    public CategoryEntity create(@Valid @RequestBody CategoryDTO dto) {
        return categoryService.createCategory(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CategoryDTO dto) {
        try {
            return ResponseEntity.ok(categoryService.updateCategory(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(categoryService.deleteCategory(id));
        } catch (RuntimeException e) {
            if (e instanceof IllegalStateException) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
            return ResponseEntity.notFound().build();
        }
    }
}
