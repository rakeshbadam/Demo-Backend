package com.example.backend.controller;

import com.example.backend.dto.TransactionDTO;
import com.example.backend.pagination.CursorPage;
import com.example.backend.service.TransactionService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // =========================================
    // POST - Create Transaction
    // =========================================

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(
            @Valid @RequestBody TransactionDTO transactionDTO) {

        TransactionDTO createdTransaction =
                transactionService.createTransaction(transactionDTO);

        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    // =========================================
    // GET - All Transactions (non-paginated)
    // =========================================

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        return ResponseEntity.ok(
                transactionService.getAllTransactions()
        );
    }

    // =========================================
    // GET - Cursor Based Pagination (NEW)
    // Example:
    // /api/transactions/cursor?size=5
    // /api/transactions/cursor?cursor=10&size=5
    // =========================================

    @GetMapping("/cursor")
    public ResponseEntity<CursorPage<TransactionDTO>> getTransactionsCursor(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                transactionService.getPage(cursor, size)
        );
    }

    // =========================================
    // GET - Transaction By ID
    // =========================================

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                transactionService.getTransactionById(id)
        );
    }

    // =========================================
    // GET - Transactions By Account
    // =========================================

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByAccountId(
            @PathVariable Long accountId) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByAccountId(accountId)
        );
    }

    // =========================================
    // GET - Transactions by Account + Date Range
    // Example:
    // /api/transactions/account/10/range?startDate=2026-01-01T00:00:00&endDate=2026-02-18T23:59:59
    // =========================================

    @GetMapping("/account/{accountId}/range")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByAccountIdAndRange(
            @PathVariable Long accountId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByAccountIdAndDateRange(
                        accountId, startDate, endDate
                )
        );
    }

    // =========================================
    // PUT - Update Transaction
    // =========================================

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionDTO transactionDTO) {

        return ResponseEntity.ok(
                transactionService.updateTransaction(id, transactionDTO)
        );
    }

    // =========================================
    // DELETE - Delete Transaction
    // =========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(
            @PathVariable Long id) {

        transactionService.deleteTransaction(id);
        return ResponseEntity.ok("Transaction deleted successfully");
    }
}