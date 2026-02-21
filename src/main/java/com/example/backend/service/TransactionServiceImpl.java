package com.example.backend.service;

import com.example.backend.dto.TransactionDTO;
import com.example.backend.entity.Account;
import com.example.backend.entity.Transaction;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    
    // CREATE (auto balance update)
 
    @Override
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {

        Account account = accountRepository.findById(transactionDTO.getAccountId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found with id: " + transactionDTO.getAccountId())
                );

        Transaction transaction = mapToEntity(transactionDTO);
        transaction.setAccount(account);

        // Apply balance impact
        applyImpact(account, transaction.getTransactionType(), transaction.getAmount());

        // Save both
        accountRepository.save(account);
        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToDTO(savedTransaction);
    }

    // GET ALL
    @Override
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // GET BY ID
    @Override
    public TransactionDTO getTransactionById(Long id) {

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Transaction not found with id: " + id)
                );

        return mapToDTO(transaction);
    }

    // GET BY ACCOUNT ID
    @Override
    public List<TransactionDTO> getTransactionsByAccountId(Long accountId) {

        return transactionRepository.findByAccountAccountId(accountId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // GET BY ACCOUNT + DATE RANGE (for export batches)
    @Override
    public List<TransactionDTO> getTransactionsByAccountIdAndDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {

        return transactionRepository.findByAccountAccountIdAndTransactionDateBetween(accountId, startDate, endDate)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    
    // UPDATE (reverse old impact, apply new)
   
    @Override
    public TransactionDTO updateTransaction(Long id, TransactionDTO transactionDTO) {

        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Transaction not found with id: " + id)
                );

        Account account = existingTransaction.getAccount();

        // 1) Reverse old impact
        reverseImpact(account, existingTransaction.getTransactionType(), existingTransaction.getAmount());

        // 2) Apply new values
        existingTransaction.setAmount(transactionDTO.getAmount());
        existingTransaction.setTransactionType(transactionDTO.getTransactionType());
        existingTransaction.setTransactionDate(transactionDTO.getTransactionDate());
        existingTransaction.setDescription(transactionDTO.getDescription());

        // 3) Apply new impact
        applyImpact(account, existingTransaction.getTransactionType(), existingTransaction.getAmount());

        // Save both
        accountRepository.save(account);
        Transaction updatedTransaction = transactionRepository.save(existingTransaction);

        return mapToDTO(updatedTransaction);
    }

   
    // DELETE 
    
    @Override
    public void deleteTransaction(Long id) {

        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Transaction not found with id: " + id)
                );

        Account account = existingTransaction.getAccount();

        // Reverse impact
        reverseImpact(account, existingTransaction.getTransactionType(), existingTransaction.getAmount());

        accountRepository.save(account);
        transactionRepository.deleteById(id);
    }

   
   
  
// BALANCE IMPACT HELPERS

private void applyImpact(Account account, String type, BigDecimal amount) {

    if (amount == null) amount = BigDecimal.ZERO;

    if ("CREDIT".equalsIgnoreCase(type)) {
        account.setCurrentBalance(account.getCurrentBalance().add(amount));
    } 
    else if ("DEBIT".equalsIgnoreCase(type) || "LOAN".equalsIgnoreCase(type)) {
        // LOAN behaves same as DEBIT
        account.setCurrentBalance(account.getCurrentBalance().subtract(amount));
    } 
    else {
        throw new IllegalArgumentException("Invalid transaction type: " + type + 
                " (must be CREDIT, DEBIT, or LOAN)");
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
        throw new IllegalArgumentException("Invalid transaction type: " + type + 
                " (must be CREDIT, DEBIT, or LOAN)");
    }
}


   
    // ENTITY → DTO
   
    private TransactionDTO mapToDTO(Transaction transaction) {
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

   
    // DTO → ENTITY
   
    private Transaction mapToEntity(TransactionDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setTransactionType(dto.getTransactionType());
        transaction.setTransactionDate(dto.getTransactionDate());
        transaction.setDescription(dto.getDescription());
        return transaction;
    }
}
