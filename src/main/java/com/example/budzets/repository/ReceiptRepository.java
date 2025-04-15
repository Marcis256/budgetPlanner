package com.example.budzets.repository;

import com.example.budzets.model.Receipt;
import com.example.budzets.model.ReceiptEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<ReceiptEntity, Long> {

    Optional<ReceiptEntity> findByReceiptNumber(String receiptNumber);

    boolean existsByReceiptNumber(String receiptNumber);

    @Query("SELECT SUM(r.total) FROM ReceiptEntity r")
    Double findTotalSpent();

    @Query("SELECT r FROM ReceiptEntity r WHERE r.date BETWEEN :start AND :end")
    List<ReceiptEntity> findByDateBetween(LocalDateTime start, LocalDateTime end, Sort sort);

    @Query("SELECT SUM(r.total) FROM ReceiptEntity r WHERE r.date BETWEEN :start AND :end")
    Double findTotalSpentBetween(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end);
}
