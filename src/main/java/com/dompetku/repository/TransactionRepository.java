package com.dompetku.repository;

import com.dompetku.entity.Transaction;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    // Mengambil transaksi berdasarkan periode tanggal
    List<Transaction> findByTransactionDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    // Pencarian dan filter transaksi
    @Query("""
        SELECT t FROM Transaction t
        WHERE
            (:keyword IS NULL OR :keyword = ''
                OR LOWER(t.description) LIKE
                   LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.category) LIKE
                   LOWER(CONCAT('%', :keyword, '%')))
        AND (:type IS NULL OR :type = '' OR t.type = :type)
        AND (:category IS NULL OR :category = ''
             OR t.category = :category)
        AND (:startDate IS NULL OR t.transactionDate >= :startDate)
        AND (:endDate IS NULL OR t.transactionDate <= :endDate)
        ORDER BY t.transactionDate DESC
    """)
    List<Transaction> searchAndFilter(
            @Param("keyword") String keyword,
            @Param("type") String type,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}