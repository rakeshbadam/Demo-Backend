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

    @Query(value = """
        SELECT 
            DATE(t.transaction_date) as txnDate,
            SUM(CASE WHEN t.transaction_type = 'CREDIT' THEN t.amount ELSE 0 END) as totalCredits,
            SUM(CASE WHEN t.transaction_type = 'DEBIT' THEN t.amount ELSE 0 END) as totalDebits,
            SUM(CASE WHEN t.transaction_type = 'LOAN' THEN t.amount ELSE 0 END) as totalEmi
        FROM transactions t
        JOIN accounts a ON t.account_id = a.account_id
        WHERE a.customer_id = :customerId
        AND t.transaction_date >= NOW() - INTERVAL 3 MONTH
        GROUP BY DATE(t.transaction_date)
        ORDER BY txnDate ASC
    """, nativeQuery = true)
    List<Object[]> getDailyTotals(@Param("customerId") Long customerId);
    @Query(value = """
        SELECT 
            MIN(DATE(t.transaction_date)) as week_start,
            SUM(CASE WHEN UPPER(t.transaction_type) = 'CREDIT' THEN t.amount ELSE 0 END) as total_income,
            SUM(CASE WHEN UPPER(t.transaction_type) = 'DEBIT' THEN t.amount ELSE 0 END) as total_debits,
            SUM(CASE WHEN UPPER(t.transaction_type) IN ('LOAN', 'EMI') THEN t.amount ELSE 0 END) as total_emi
        FROM transactions t
        JOIN accounts a ON t.account_id = a.account_id
        WHERE a.customer_id = :customerId
        AND t.transaction_date >= :fromDate
        GROUP BY YEARWEEK(t.transaction_date)
        ORDER BY week_start ASC
    """, nativeQuery = true)
    List<Object[]> getWeeklyTotals(
            @Param("customerId") Long customerId,
            @Param("fromDate") LocalDateTime fromDate
    );
}