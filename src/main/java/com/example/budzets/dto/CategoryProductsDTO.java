package com.example.budzets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryProductsDTO {
    private String category;
    private List<ProductWithTotalDTO> products;
}