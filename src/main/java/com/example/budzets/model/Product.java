package com.example.budzets.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    private Long id;
    private String name;
    private double unitPrice;
    private double quantity;
    private double totalPrice;
    private Double discountAmount;
}
