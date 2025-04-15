package com.example.budzets.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "receipts")
@ToString
public class ReceiptEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String receiptNumber;

    private LocalDateTime date;

    private double total;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CheckProductEntity> products;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public double getTotal() {
        return total;
    }

    public List<CheckProductEntity> getProducts() {
        return products;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setProducts(List<CheckProductEntity> products) {
        this.products = products;
    }
}