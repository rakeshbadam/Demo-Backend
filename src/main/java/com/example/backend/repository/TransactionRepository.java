package com.example.backend.repository;

import com.example.backend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Existing
    List<Transaction> findByAccountAccountId(Long accountId);

    // Existing
    List<Transaction> findByAccountAccountIdAndTransactionDateBetween(
            Long accountId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // 🔥 REQUIRED for DTI (Loan transactions after given date)
    List<Transaction> findByAccountAccountIdAndTransactionTypeAndTransactionDateAfter(
            Long accountId,
            String transactionType,
            LocalDateTime date
    );
}
