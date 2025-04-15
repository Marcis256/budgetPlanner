package com.example.budzets.dto;

import java.util.List;

public class CategoryProductsDTO {
    private String category;
    private List<ProductWithTotalDTO> products;

    public CategoryProductsDTO(String category, List<ProductWithTotalDTO> products) {
        this.category = category;
        this.products = products;
    }

    public String getCategory() {
        return category;
    }

    public List<ProductWithTotalDTO> getProducts() {
        return products;
    }
}
