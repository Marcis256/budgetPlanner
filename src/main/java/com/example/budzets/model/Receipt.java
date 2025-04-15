package com.example.budzets.model;

import java.time.LocalDateTime;
import java.util.List;

public class Receipt {
    private String receiptNumber;
    private LocalDateTime date;
    private List<Product> products;
    private double total;

    // Getters and setters

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Čeks ").append(receiptNumber).append(" (").append(date).append(")\n");
        for (Product product : products) {
            sb.append(" - ").append(product).append("\n");
        }
        sb.append("Kopā: ").append(total).append(" EUR");
        return sb.toString();
    }
}