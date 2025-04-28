package com.example.budzets.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryUpdateDTO {
    private Long productId;
    private Long categoryId;
}
