package com.example.backend.service;

import com.example.backend.dto.TransactionDTO;
import com.example.backend.pagination.CursorPage;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {

    TransactionDTO createTransaction(TransactionDTO transactionDTO);

    List<TransactionDTO> getAllTransactions();

    TransactionDTO getTransactionById(Long id);

    List<TransactionDTO> getTransactionsByAccountId(Long accountId);

    List<TransactionDTO> getTransactionsByAccountIdAndDateRange(
            Long accountId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    TransactionDTO updateTransaction(Long id, TransactionDTO transactionDTO);

    void deleteTransaction(Long id);

    // ✅ ADD THIS METHOD
    CursorPage<TransactionDTO> getPage(Long cursor, int size);
}