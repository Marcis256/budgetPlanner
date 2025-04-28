package com.example.budzets.service;

import com.example.budzets.dto.CategoryDTO;
import com.example.budzets.model.CategoryEntity;
import com.example.budzets.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryEntity> getAllCategories() {
        return categoryRepository.findAll();
    }

    public CategoryEntity createCategory(CategoryDTO dto) {
        CategoryEntity category = new CategoryEntity();
        category.setName(dto.getName());
        return categoryRepository.save(category);
    }

    public CategoryEntity updateCategory(Long id, CategoryDTO dto) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategorija nav atrasta: ID = " + id));
        category.setName(dto.getName());
        return categoryRepository.save(category);
    }

    public String deleteCategory(Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategorija nav atrasta: ID = " + id));

        if (!CollectionUtils.isEmpty(category.getProducts())) {
            throw new IllegalStateException("❌ Nevar dzēst kategoriju, kurai piesaistīti produkti.");
        }

        categoryRepository.deleteById(id);
        return "✅ Kategorija veiksmīgi dzēsta.";
    }

    public CategoryEntity findByIdOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategorija nav atrasta: ID = " + id));
    }
}
