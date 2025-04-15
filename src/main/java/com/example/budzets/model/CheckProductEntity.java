package com.example.budzets.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "check_products")
public class CheckProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double quantity;

    private double totalPrice;

    private Double discountAmount;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({"checkProducts"})
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "receipt_id")
    @JsonIgnoreProperties({"products"})
    private ReceiptEntity receipt;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
    public Double getDiscountAmount() {
        return discountAmount;
    }


    public ProductEntity getProduct() {
        return product;
    }

    public ReceiptEntity getReceipt() {
        return receipt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public void setReceipt(ReceiptEntity receipt) {
        this.receipt = receipt;
    }
}