package com.example.backend.service;

import com.example.backend.dto.CreateExportBatchDTO;
import com.example.backend.dto.ExportBatchDTO;
import com.example.backend.dto.UpdateExportBatchStatusDTO;
import com.example.backend.entity.Account;
import com.example.backend.entity.Customer;
import com.example.backend.entity.ExportBatch;
import com.example.backend.entity.Transaction;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.ExportBatchRepository;
import com.example.backend.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExportBatchServiceImpl implements ExportBatchService {

    private final ExportBatchRepository exportBatchRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LambdaInvocationService lambdaInvocationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExportBatchServiceImpl(
            ExportBatchRepository exportBatchRepository,
            CustomerRepository customerRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            LambdaInvocationService lambdaInvocationService) {

        this.exportBatchRepository = exportBatchRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.lambdaInvocationService = lambdaInvocationService;
    }

    // =====================================================
    // CREATE EXPORT BATCH (Manual)
    // =====================================================
    @Override
    public ExportBatchDTO createExportBatch(CreateExportBatchDTO dto) {

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId()));

        ExportBatch batch = new ExportBatch();
        batch.setCustomer(customer);
        batch.setStartDate(dto.getStartDate());
        batch.setEndDate(dto.getEndDate());
        batch.setStatus("PENDING");

        return mapToDTO(exportBatchRepository.save(batch));
    }

    // =====================================================
    // CREATE LAST 3 MONTHS BATCH + LAMBDA CALL
    // =====================================================
    @Override
    public ExportBatchDTO createLast3MonthsBatch(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + customerId));

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusMonths(3);

        ExportBatch batch = new ExportBatch();
        batch.setCustomer(customer);
        batch.setStartDate(start);
        batch.setEndDate(end);
        batch.setStatus("PROCESSING");

        ExportBatch savedBatch = exportBatchRepository.save(batch);

        try {

            Map<String, Object> analytics =
                    calculateCustomerAnalytics(customer);

            Map<String, Object> payload = new HashMap<>(analytics);
            payload.put("customerId", customerId);
            payload.put("batchId", savedBatch.getBatchId());
            payload.put("startDate", start.toString());
            payload.put("endDate", end.toString());

            String jsonPayload = objectMapper.writeValueAsString(payload);

            String lambdaResponse = lambdaInvocationService.invoke(
                    "banking-analytics-export",
                    jsonPayload
            );

            Map<String, Object> responseMap =
                    objectMapper.readValue(lambdaResponse, Map.class);

            String body = (String) responseMap.get("body");
            Map<String, Object> bodyMap =
                    objectMapper.readValue(body, Map.class);

            String fileKey = (String) bodyMap.get("fileKey");

            if (fileKey == null) {
                throw new RuntimeException("fileKey not found in Lambda response");
            }

            savedBatch.setStatus("COMPLETED");
            savedBatch.setFilePath(fileKey);
            savedBatch.setErrorMessage(null);

        } catch (Exception e) {

            savedBatch.setStatus("FAILED");
            savedBatch.setErrorMessage(e.getMessage());
        }

        return mapToDTO(exportBatchRepository.save(savedBatch));
    }

    // =====================================================
    // 🔥 ANALYTICS FOR ALL CUSTOMERS
    // =====================================================
    @Override
    public List<Map<String, Object>> getAllCustomerAnalytics() {

        List<Customer> customers = customerRepository.findAll();

        return customers.stream()
                .map(this::calculateCustomerAnalytics)
                .collect(Collectors.toList());
    }

    // =====================================================
    // 🔥 CORE ANALYTICS LOGIC
    // =====================================================
    private Map<String, Object> calculateCustomerAnalytics(Customer customer) {

        List<Account> accounts =
                accountRepository.findByCustomerCustomerId(customer.getCustomerId());

        BigDecimal totalCreditLimit = BigDecimal.ZERO;
        BigDecimal totalCreditBalance = BigDecimal.ZERO;

        for (Account account : accounts) {

            if ("CREDIT_CARD".equalsIgnoreCase(account.getAccountType())) {

                if (account.getCreditLimit() != null)
                    totalCreditLimit = totalCreditLimit.add(account.getCreditLimit());

                if (account.getCurrentBalance() != null)
                    totalCreditBalance = totalCreditBalance.add(account.getCurrentBalance());
            }
        }

        // ==============================
        // DTI USING LOAN TRANSACTIONS
        // ==============================

        BigDecimal totalMonthlyLoanPayments = BigDecimal.ZERO;
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        for (Account account : accounts) {

            List<Transaction> loanTransactions =
                    transactionRepository
                            .findByAccountAccountIdAndTransactionTypeAndTransactionDateAfter(
                                    account.getAccountId(),
                                    "LOAN",
                                    oneMonthAgo
                            );

            for (Transaction txn : loanTransactions) {
                if (txn.getAmount() != null) {
                    totalMonthlyLoanPayments =
                            totalMonthlyLoanPayments.add(txn.getAmount());
                }
            }
        }

        BigDecimal annualIncome =
                customer.getIncome() != null ? customer.getIncome() : BigDecimal.ZERO;

        BigDecimal monthlyIncome =
                annualIncome.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

        BigDecimal creditUtilization = BigDecimal.ZERO;
        if (totalCreditLimit.compareTo(BigDecimal.ZERO) > 0) {
            creditUtilization = totalCreditBalance
                    .divide(totalCreditLimit, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal dti = BigDecimal.ZERO;
        if (monthlyIncome.compareTo(BigDecimal.ZERO) > 0) {
            dti = totalMonthlyLoanPayments
                    .divide(monthlyIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("customerId", customer.getCustomerId());
        result.put("annualIncome", annualIncome);
        result.put("monthlyIncome", monthlyIncome);
        result.put("creditUtilization", creditUtilization);
        result.put("dti", dti);
        result.put("accounts", accounts.size());
        result.put("generatedAt", LocalDateTime.now().toString());

        return result;
    }

    // =====================================================
    // GET METHODS
    // =====================================================
    @Override
    public List<ExportBatchDTO> getAllBatches() {
        return exportBatchRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExportBatchDTO getBatchById(Long batchId) {
        ExportBatch batch = exportBatchRepository.findById(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Batch not found with id: " + batchId));
        return mapToDTO(batch);
    }

    @Override
    public List<ExportBatchDTO> getBatchesByCustomerId(Long customerId) {
        return exportBatchRepository.findByCustomerCustomerId(customerId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExportBatchDTO> getPendingBatches() {
        return exportBatchRepository.findByStatus("PENDING")
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExportBatchDTO updateBatchStatus(Long batchId, UpdateExportBatchStatusDTO dto) {

        ExportBatch batch = exportBatchRepository.findById(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Batch not found with id: " + batchId));

        batch.setStatus(dto.getStatus());
        batch.setErrorMessage(dto.getErrorMessage());
        batch.setFilePath(dto.getFilePath());

        return mapToDTO(exportBatchRepository.save(batch));
    }

    @Override
    public void deleteBatch(Long batchId) {

        ExportBatch batch = exportBatchRepository.findById(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Batch not found with id: " + batchId));

        exportBatchRepository.delete(batch);
    }

    private ExportBatchDTO mapToDTO(ExportBatch batch) {

        ExportBatchDTO dto = new ExportBatchDTO();
        dto.setBatchId(batch.getBatchId());
        dto.setCustomerId(batch.getCustomer().getCustomerId());
        dto.setStartDate(batch.getStartDate());
        dto.setEndDate(batch.getEndDate());
        dto.setStatus(batch.getStatus());
        dto.setFilePath(batch.getFilePath());
        dto.setErrorMessage(batch.getErrorMessage());
        dto.setCreatedTime(batch.getCreatedTime());
        dto.setModifiedTime(batch.getModifiedTime());

        return dto;
    }
}
