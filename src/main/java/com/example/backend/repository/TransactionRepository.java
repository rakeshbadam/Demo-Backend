package com.example.backend.repository;

import com.example.backend.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountAccountId(Long accountId);

    List<Transaction> findByAccountAccountIdAndTransactionDateBetween(
            Long accountId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<Transaction> findByAccountAccountIdAndTransactionTypeAndTransactionDateAfter(
            Long accountId,
            String transactionType,
            LocalDateTime date
    );

    // ✅ Cursor-based pagination (by transactionId)
    @Query("""
           SELECT t FROM Transaction t
           WHERE (:cursor IS NULL OR t.transactionId > :cursor)
           ORDER BY t.transactionId ASC
           """)
    List<Transaction> findAfterCursor(@Param("cursor") Long cursor, Pageable pageable);
}