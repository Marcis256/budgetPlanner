package com.example.budzets.dto;

public class ProductCategoryUpdateDTO {
    private Long productId;
    private Long categoryId;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}