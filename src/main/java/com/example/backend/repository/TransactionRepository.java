package com.example.backend.repository;

import com.example.backend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
