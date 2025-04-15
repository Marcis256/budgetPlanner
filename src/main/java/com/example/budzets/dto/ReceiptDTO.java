package com.example.budzets.dto;
import java.time.LocalDateTime;
import java.util.List;

public class ReceiptDTO {
    private String receiptNumber;
    private LocalDateTime date;
    private double total;
    private List<CheckProductDTO> products;

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

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<CheckProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<CheckProductDTO> products) {
        this.products = products;
    }
}