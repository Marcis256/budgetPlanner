package com.example.budzets.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "unitPrice"})
})
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double unitPrice;

    public ProductEntity() {
    }

    public ProductEntity(String name, double unitPrice) {
        this.name = name;
        this.unitPrice = unitPrice;
    }

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CheckProductEntity> checkProducts;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties("products")
    private CategoryEntity category;

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public double getUnitPrice() { return unitPrice; }
    public Set<CheckProductEntity> getCheckProducts() { return checkProducts; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public void setCheckProducts(Set<CheckProductEntity> checkProducts) {
        this.checkProducts = checkProducts;
    }
}
