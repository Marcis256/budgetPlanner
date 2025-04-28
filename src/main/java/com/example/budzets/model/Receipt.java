package com.example.budzets.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receipt {
    private String receiptNumber;
    private LocalDateTime date;
    private List<Product> products;
    private double total;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Čeks ").append(receiptNumber).append(" (").append(date).append(")\n");
        if (products != null) {
            for (Product product : products) {
                sb.append(" - ").append(product).append("\n");
            }
        }
        sb.append("Kopā: ").append(total).append(" EUR");
        return sb.toString();
    }
}
