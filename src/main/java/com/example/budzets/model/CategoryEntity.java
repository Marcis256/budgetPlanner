package com.example.budzets.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "categories")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // piem. "Pārtika", "Dzērieni", "Sadzīve" utt.

    @OneToMany(mappedBy = "category")
    private Set<ProductEntity> products;

    // Getters and Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public Set<ProductEntity> getProducts() { return products; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setProducts(Set<ProductEntity> products) { this.products = products; }
}
