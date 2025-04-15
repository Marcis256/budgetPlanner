package com.example.budzets.dto;

public class ProductWithTotalDTO {
    private String name;
    private double unitPrice;
    private double quantity;
    private double total;
    private double discountAmount; // ✅ Jauns lauks

    public ProductWithTotalDTO(String name, double unitPrice, double quantity, double total, double discountAmount) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.total = total;
        this.discountAmount = discountAmount;
    }

    // Vecais konstruktors priekš savietojamības (ja nepieciešams)
    public ProductWithTotalDTO(String name, double unitPrice, double quantity, double total) {
        this(name, unitPrice, quantity, total, 0.0);
    }

    // Getteri
    public String getName() {
        return name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getTotal() {
        return total;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    // Setteri (ja vajag)
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }
}