package com.example.budzets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithTotalDTO {
    private String name;
    private double unitPrice;
    private double quantity;
    private double total;
    private double discountAmount;
}
