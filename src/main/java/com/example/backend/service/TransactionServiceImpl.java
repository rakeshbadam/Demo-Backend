package com.example.backend.service;

import com.example.backend.dto.TransactionDTO;
import com.example.backend.entity.Account;
import com.example.backend.entity.Transaction;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.pagination.AbstractCursorService;
import com.example.backend.pagination.CursorPage;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.TransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl
    extends AbstractCursorService<Transaction, TransactionDTO>
    implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    // ===============================
    // CURSOR METHODS (Required)
    // ===============================

    @Override
    protected List<Transaction> fetchAfterCursor(Long cursor, Pageable pageable) {
        return transactionRepository.findAfterCursor(cursor, pageable);
    }

    @Override
    protected Long extractId(Transaction entity) {
        return entity.getTransactionId();
    }

    @Override
    protected TransactionDTO mapToDTO(Transaction entity) {
        return convertToDTO(entity);
    }

    // ===============================
    // CREATE (auto balance update)
    // ===============================

    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {

        Account account = accountRepository.findById(transactionDTO.getAccountId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found with id: " + transactionDTO.getAccountId())
                );

        Transaction transaction = mapToEntity(transactionDTO);
        transaction.setAccount(account);

        applyImpact(account, transaction.getTransactionType(), transaction.getAmount());

        accountRepository.save(account);
        Transaction savedTransaction = transactionRepository.save(transaction);

        return convertToDTO(savedTransaction);
    }

    // ===============================
    // GET ALL
    // ===============================

    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ===============================
    // GET BY ID
    // ===============================

    public TransactionDTO getTransactionById(Long id) {

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Transaction not found with id: " + id)
                );

        return convertToDTO(transaction);
    }

    // ===============================
    // GET BY ACCOUNT ID
    // ===============================

    public List<TransactionDTO> getTransactionsByAccountId(Long accountId) {

        return transactionRepository.findByAccountAccountId(accountId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ===============================
    // GET BY ACCOUNT + DATE RANGE
    // ===============================

    public List<TransactionDTO> getTransactionsByAccountIdAndDateRange(
            Long accountId,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        return transactionRepository
                .findByAccountAccountIdAndTransactionDateBetween(accountId, startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ===============================
    // UPDATE
    // ===============================

    public TransactionDTO updateTransaction(Long id, TransactionDTO transactionDTO) {

        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Transaction not found with id: " + id)
                );

        Account account = existingTransaction.getAccount();

        reverseImpact(account,
                existingTransaction.getTransactionType(),
                existingTransaction.getAmount());

        existingTransaction.setAmount(transactionDTO.getAmount());
        existingTransaction.setTransactionType(transactionDTO.getTransactionType());
        existingTransaction.setTransactionDate(transactionDTO.getTransactionDate());
        existingTransaction.setDescription(transactionDTO.getDescription());

        applyImpact(account,
                existingTransaction.getTransactionType(),
                existingTransaction.getAmount());

        accountRepository.save(account);
        Transaction updatedTransaction = transactionRepository.save(existingTransaction);

        return convertToDTO(updatedTransaction);
    }

    // ===============================
    // DELETE
    // ===============================

    public void deleteTransaction(Long id) {

        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Transaction not found with id: " + id)
                );

        Account account = existingTransaction.getAccount();

        reverseImpact(account,
                existingTransaction.getTransactionType(),
                existingTransaction.getAmount());

        accountRepository.save(account);
        transactionRepository.deleteById(id);
    }

    // ===============================
    // BALANCE IMPACT HELPERS
    // ===============================

    private void applyImpact(Account account, String type, BigDecimal amount) {

        if (amount == null) amount = BigDecimal.ZERO;

        if ("CREDIT".equalsIgnoreCase(type)) {
            account.setCurrentBalance(account.getCurrentBalance().add(amount));
        }
        else if ("DEBIT".equalsIgnoreCase(type) || "LOAN".equalsIgnoreCase(type)) {
            account.setCurrentBalance(account.getCurrentBalance().subtract(amount));
        }
        else {
            throw new IllegalArgumentException(
                    "Invalid transaction type: " + type +
                    " (must be CREDIT, DEBIT, or LOAN)"
            );
        }
    }

    private void reverseImpact(Account account, String type, BigDecimal amount) {

        if (amount == null) amount = BigDecimal.ZERO;

        if ("CREDIT".equalsIgnoreCase(type)) {
            account.setCurrentBalance(account.getCurrentBalance().subtract(amount));
        }
        else if ("DEBIT".equalsIgnoreCase(type) || "LOAN".equalsIgnoreCase(type)) {
            account.setCurrentBalance(account.getCurrentBalance().add(amount));
        }
        else {
            throw new IllegalArgumentException(
                    "Invalid transaction type: " + type +
                    " (must be CREDIT, DEBIT, or LOAN)"
            );
        }
    }

    // ===============================
    // ENTITY → DTO
    // ===============================

    private TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setAccountId(transaction.getAccount().getAccountId());
        dto.setAmount(transaction.getAmount());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setDescription(transaction.getDescription());
        dto.setCreatedTime(transaction.getCreatedTime());
        dto.setModifiedTime(transaction.getModifiedTime());
        return dto;
    }

    // ===============================
    // DTO → ENTITY
    // ===============================

    private Transaction mapToEntity(TransactionDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setTransactionType(dto.getTransactionType());
        transaction.setTransactionDate(dto.getTransactionDate());
        transaction.setDescription(dto.getDescription());
        return transaction;
    }
    @Override
public CursorPage<TransactionDTO> getPage(Long cursor, int size) {
    return super.getPage(cursor, size);
}
}