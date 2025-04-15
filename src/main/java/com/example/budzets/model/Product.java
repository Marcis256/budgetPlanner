package com.example.budzets.model;

public class Product {
    private Long id; // ← Ja produkts nāk no DB, tam būs ID
    private String name;
    private double unitPrice;
    private double quantity;
    private double totalPrice;
    private Double discountAmount;

    public Product() {
    }

    public Product(String name, double unitPrice, double quantity) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    // Getter & Setter for id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getters & Setters
    public String getName() {
        return name;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return name + " (" + quantity + " x " + unitPrice + " EUR) = " + getTotalPrice() + " EUR";
    }
}
