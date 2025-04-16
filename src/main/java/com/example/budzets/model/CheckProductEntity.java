package com.example.budzets.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "check_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double quantity;
    private double totalPrice;
    private Double discountAmount;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties("checkProducts")
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "receipt_id")
    @JsonIgnoreProperties("products")
    private ReceiptEntity receipt;
}
