package com.example.backend.service;

import com.example.backend.dto.AnalyticsDTO;
import com.example.backend.dto.DailyTotalsDTO;
import com.example.backend.dto.WeeklySummaryDTO;
import com.example.backend.entity.Account;
import com.example.backend.entity.Customer;
import com.example.backend.entity.Transaction;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

   
    // CREDIT UTILIZATION
    
    public AnalyticsDTO calculateCreditUtilization(Long customerId) {

        customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + customerId)
                );

        List<Account> accounts = accountRepository.findByCustomerCustomerId(customerId);

        BigDecimal totalLimit = BigDecimal.ZERO;
        BigDecimal totalBalance = BigDecimal.ZERO;

        for (Account account : accounts) {

            if ("CREDIT_CARD".equalsIgnoreCase(account.getAccountType())) {

                if (account.getCreditLimit() != null)
                    totalLimit = totalLimit.add(account.getCreditLimit());

                if (account.getCurrentBalance() != null)
                    totalBalance = totalBalance.add(account.getCurrentBalance());
            }
        }

        BigDecimal utilization = BigDecimal.ZERO;

        if (totalLimit.compareTo(BigDecimal.ZERO) > 0) {
            utilization = totalBalance
                    .divide(totalLimit, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        AnalyticsDTO dto = new AnalyticsDTO();
        dto.setCustomerId(customerId);
        dto.setValue(utilization.setScale(2, RoundingMode.HALF_UP));
        dto.setMetricType("CREDIT_UTILIZATION");

        return dto;
    }

   
    // DTI with Risk Category
   
    @Override
    public AnalyticsDTO calculateDTI(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + customerId)
                );

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(3);

        List<Transaction> transactions = transactionRepository.findAll();

        BigDecimal totalLoanPayments = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {

            if (transaction.getAccount().getCustomer().getCustomerId().equals(customerId)
                    && "LOAN".equalsIgnoreCase(transaction.getTransactionType())
                    && transaction.getTransactionDate().isAfter(startDate)
                    && transaction.getTransactionDate().isBefore(endDate)) {

                totalLoanPayments = totalLoanPayments.add(transaction.getAmount());
            }
        }

        BigDecimal averageMonthlyEMI =
                totalLoanPayments.divide(BigDecimal.valueOf(3), 4, RoundingMode.HALF_UP);

        BigDecimal monthlyIncome =
                customer.getIncome().divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP);

        BigDecimal dti = BigDecimal.ZERO;

        if (monthlyIncome.compareTo(BigDecimal.ZERO) > 0) {
            dti = averageMonthlyEMI
                    .divide(monthlyIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        dti = dti.setScale(2, RoundingMode.HALF_UP);

        AnalyticsDTO dto = new AnalyticsDTO();
        dto.setCustomerId(customerId);
        dto.setValue(dti);
        dto.setMetricType("DTI");
        dto.setRiskCategory(determineRiskCategory(dti));

        return dto;
    }

 
    // RISK CATEGORY LOGIC
    
    private String determineRiskCategory(BigDecimal dti) {

        if (dti.compareTo(BigDecimal.valueOf(20)) <= 0)
            return "LOW";

        if (dti.compareTo(BigDecimal.valueOf(35)) <= 0)
            return "MEDIUM";

        if (dti.compareTo(BigDecimal.valueOf(50)) <= 0)
            return "HIGH";

        return "VERY_HIGH";
    }

   
    // ALL CUSTOMERS DTI
   
    @Override
    public List<AnalyticsDTO> calculateAllCustomersDTI() {

        List<Customer> customers = customerRepository.findAll();
        List<AnalyticsDTO> results = new ArrayList<>();

        for (Customer customer : customers) {
            results.add(calculateDTI(customer.getCustomerId()));
        }

        return results;
    }

    
    // ALL CUSTOMERS CREDIT UTILIZATION
    
    @Override
    public List<AnalyticsDTO> calculateAllCustomersCreditUtilization() {

        List<Customer> customers = customerRepository.findAll();
        List<AnalyticsDTO> results = new ArrayList<>();

        for (Customer customer : customers) {
            results.add(calculateCreditUtilization(customer.getCustomerId()));
        }

        return results;
    }

    // DAILY TOTALS
    @Override
    public List<DailyTotalsDTO> getDailyTotals(Long customerId) {

        customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + customerId)
                );

        List<Object[]> results = transactionRepository.getDailyTotals(customerId);

        return results.stream()
                .map(r -> {

                    LocalDate date;

                    if (r[0] instanceof java.sql.Date sqlDate) {
                        date = sqlDate.toLocalDate();
                    } else if (r[0] instanceof java.sql.Timestamp ts) {
                        date = ts.toLocalDateTime().toLocalDate();
                    } else {
                        date = (LocalDate) r[0];
                    }

                    BigDecimal credits = r[1] != null ? (BigDecimal) r[1] : BigDecimal.ZERO;
                    BigDecimal debits  = r[2] != null ? (BigDecimal) r[2] : BigDecimal.ZERO;
                    BigDecimal emi     = r[3] != null ? (BigDecimal) r[3] : BigDecimal.ZERO;

                    return new DailyTotalsDTO(date, credits, debits, emi);
                })
                .toList();
    }

    // WEEKLY SUMMARY
    @Override
    public List<WeeklySummaryDTO> getWeeklySummary(Long customerId) {

        customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + customerId)
                );

        LocalDateTime fromDate = LocalDateTime.now().minusMonths(3);

        List<Object[]> results = transactionRepository.getWeeklyTotals(customerId, fromDate);

        return results.stream()
                .map(r -> {

                    LocalDate weekStart;

                    Object dateObj = r[0];

                    if (dateObj instanceof java.sql.Date sqlDate) {
                        weekStart = sqlDate.toLocalDate();
                    } else if (dateObj instanceof java.sql.Timestamp ts) {
                        weekStart = ts.toLocalDateTime().toLocalDate();
                    } else if (dateObj instanceof LocalDate ld) {
                        weekStart = ld;
                    } else {
                        throw new RuntimeException("Unexpected date type: " + dateObj.getClass());
                    }

                    BigDecimal income = r[1] != null ? (BigDecimal) r[1] : BigDecimal.ZERO;
                    BigDecimal debits = r[2] != null ? (BigDecimal) r[2] : BigDecimal.ZERO;
                    BigDecimal emi    = r[3] != null ? (BigDecimal) r[3] : BigDecimal.ZERO;

                    return new WeeklySummaryDTO(weekStart, income, debits, emi);
                })
                .toList();
    }
}