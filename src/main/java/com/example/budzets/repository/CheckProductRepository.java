package com.example.budzets.repository;

import com.example.budzets.model.CheckProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CheckProductRepository extends JpaRepository<CheckProductEntity, Long> {


    @Query("""
        SELECT new map(c.name as category, SUM(cp.totalPrice) as total)
        FROM CheckProductEntity cp
        JOIN cp.product p
        JOIN p.category c
        GROUP BY c.name
    """)
    List<Map<String, Object>> getTotalSpentPerCategory();

    @Query("""
    SELECT new map(
        c.name as category,
        SUM(cp.totalPrice) as total
    )
    FROM CheckProductEntity cp
    JOIN cp.product p
    JOIN p.category c
    WHERE c.name IN :categories
      AND cp.receipt.date BETWEEN :start AND :end
    GROUP BY c.name
""")
    List<Map<String, Object>> getTotalSpentPerSelectedCategories(
            @Param("categories") List<String> categories,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
    SELECT SUM(cp.totalPrice)
    FROM CheckProductEntity cp
    JOIN cp.product p
    JOIN p.category c
    WHERE c.name IN :categories
      AND cp.receipt.date BETWEEN :start AND :end
""")
    Double getTotalForSelectedCategories(
            @Param("categories") List<String> categories,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    List<CheckProductEntity> findByReceipt_DateBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT new map(c.name as category, SUM(cp.totalPrice) as total)
    FROM CheckProductEntity cp
    JOIN cp.product p
    JOIN p.category c
    WHERE c.name IN :categories
    GROUP BY c.name
""")
    List<Map<String, Object>> getTotalSpentPerSelectedCategoriesWithoutDate(@Param("categories") List<String> categories);

    @Query("""
    SELECT SUM(cp.totalPrice)
    FROM CheckProductEntity cp
    JOIN cp.product p
    JOIN p.category c
    WHERE c.name IN :categories
""")
    Double getTotalForSelectedCategoriesWithoutDate(@Param("categories") List<String> categories);

    @Query("""
    SELECT cp FROM CheckProductEntity cp
    JOIN cp.product p
    JOIN p.category c
    WHERE c.name = :category AND cp.receipt.date BETWEEN :start AND :end
""")
    List<CheckProductEntity> findByCategoryAndDate(
            @Param("category") String category,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}