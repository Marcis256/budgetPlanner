package com.example.budzets.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CheckProductDTO {
    private Long productId;
    private String name;
    private double unitPrice;
    private double quantity;
    private double totalPrice;
    private Double discountAmount;
}
