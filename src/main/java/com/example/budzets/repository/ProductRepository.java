package com.example.budzets.repository;

import com.example.budzets.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    // Meklē pēc nosaukuma un vienības cenas
    Optional<ProductEntity> findByNameAndUnitPrice(String name, double unitPrice);

    List<ProductEntity> findByCategory_Id(Long categoryId);
    List<ProductEntity> findByCategoryIsNull();
}
