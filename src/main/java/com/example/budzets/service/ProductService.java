package com.example.budzets.service;

import com.example.budzets.dto.ProductCategoryUpdateDTO;
import com.example.budzets.model.CategoryEntity;
import com.example.budzets.model.ProductEntity;
import com.example.budzets.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.budzets.util.RoundUtil.round;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    public List<ProductEntity> getProductsByCategory(String categoryId) {
        if ("none".equalsIgnoreCase(categoryId)) {
            return productRepository.findByCategoryIsNull();
        }

        try {
            Long id = Long.parseLong(categoryId);
            return productRepository.findByCategory_Id(id);
        } catch (NumberFormatException e) {
            return List.of(); // Invalid ID
        }
    }

    public void updateCategories(List<ProductCategoryUpdateDTO> updates) {
        for (ProductCategoryUpdateDTO update : updates) {
            ProductEntity product = findByIdOrThrow(update.getProductId());

            CategoryEntity category = update.getCategoryId() != null
                    ? categoryService.findByIdOrThrow(update.getCategoryId())
                    : null;

            product.setCategory(category);
            productRepository.save(product);
        }
    }

    public void updateCategory(Long productId, Long categoryId) {
        ProductEntity product = findByIdOrThrow(productId);
        CategoryEntity category = categoryService.findByIdOrThrow(categoryId);
        product.setCategory(category);
        productRepository.save(product);
    }

    public ProductEntity findOrCreateProductByNameAndPrice(String name, double unitPrice) {
        double roundedPrice = round(unitPrice);

        return productRepository
                .findByNameAndUnitPrice(name, roundedPrice)
                .orElseGet(() -> {
                    ProductEntity newProduct = new ProductEntity();
                    newProduct.setName(name);
                    newProduct.setUnitPrice(roundedPrice);
                    return productRepository.save(newProduct);
                });
    }

    public ProductEntity findByIdOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produkta ID nav atrasts: " + id));
    }

    public ProductEntity createNewProduct(String name, double unitPrice) {
        ProductEntity product = new ProductEntity();
        product.setName(name);
        product.setUnitPrice(round(unitPrice));
        return productRepository.save(product);
    }

    public boolean deleteProducttById(Long id) {
        if (!productRepository.existsById(id)) {
            return false;
        }
        productRepository.deleteById(id);
        return true;
    }
}
