package com.example.budzets.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptDTO {
    private String receiptNumber;
    private LocalDateTime date;
    private double total;
    private List<CheckProductDTO> products;
}
