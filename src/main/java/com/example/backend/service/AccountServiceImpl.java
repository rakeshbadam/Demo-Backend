package com.example.backend.service;

import com.example.backend.dto.AccountDTO;
import com.example.backend.dto.AccountSummaryDTO;
import com.example.backend.dto.CustomerAccountOverviewDTO;
import com.example.backend.entity.Account;
import com.example.backend.entity.Customer;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // ==========================
    // CREATE
    // ==========================
    @Override
    public AccountDTO createAccount(AccountDTO accountDTO) {

        Customer customer = customerRepository.findById(accountDTO.getCustomerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + accountDTO.getCustomerId())
                );

        Account account = mapToEntity(accountDTO);
        account.setCustomer(customer);

        Account savedAccount = accountRepository.save(account);

        return mapToDTO(savedAccount);
    }

    // ==========================
    // GET ALL
    // ==========================
    @Override
    public List<AccountDTO> getAllAccounts() {

        return accountRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ==========================
    // GET BY ID
    // ==========================
    @Override
    public AccountDTO getAccountById(Long id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found with id: " + id)
                );

        return mapToDTO(account);
    }

    // ==========================
    // GET BY CUSTOMER ID
    // ==========================
    @Override
    public List<AccountDTO> getAccountsByCustomerId(Long customerId) {

        // Validate customer exists
        customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + customerId)
                );

        return accountRepository.findByCustomerCustomerId(customerId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ==========================
    // CUSTOMER ACCOUNT OVERVIEW
    // ==========================
    @Override
    public CustomerAccountOverviewDTO getCustomerAccountsOverview(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + customerId)
                );

        List<Account> accounts = accountRepository.findByCustomerCustomerId(customerId);

        List<AccountSummaryDTO> summaryList = accounts.stream()
                .map(account -> {
                    AccountSummaryDTO dto = new AccountSummaryDTO();
                    dto.setAccountId(account.getAccountId());
                    dto.setAccountType(account.getAccountType());
                    return dto;
                })
                .collect(Collectors.toList());

        CustomerAccountOverviewDTO response = new CustomerAccountOverviewDTO();
        response.setCustomerId(customerId);
        response.setTotalAccounts(summaryList.size());
        response.setAccounts(summaryList);

        return response;
    }

    // ==========================
    // UPDATE
    // ==========================
    @Override
    public AccountDTO updateAccount(Long id, AccountDTO accountDTO) {

        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found with id: " + id)
                );

        existingAccount.setAccountType(accountDTO.getAccountType());
        existingAccount.setCreditLimit(accountDTO.getCreditLimit());
        existingAccount.setCurrentBalance(accountDTO.getCurrentBalance());

        Account updatedAccount = accountRepository.save(existingAccount);

        return mapToDTO(updatedAccount);
    }

    // ==========================
    // DELETE
    // ==========================
    @Override
    public void deleteAccount(Long id) {

        if (!accountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Account not found with id: " + id);
        }

        accountRepository.deleteById(id);
    }

    // ==========================
    // ENTITY → DTO
    // ==========================
    private AccountDTO mapToDTO(Account account) {

        AccountDTO dto = new AccountDTO();
        dto.setAccountId(account.getAccountId());
        dto.setCustomerId(account.getCustomer().getCustomerId());
        dto.setAccountType(account.getAccountType());
        dto.setCreditLimit(account.getCreditLimit());
        dto.setCurrentBalance(account.getCurrentBalance());
        dto.setCreatedTime(account.getCreatedTime());
        dto.setModifiedTime(account.getModifiedTime());

        return dto;
    }

    // ==========================
    // DTO → ENTITY
    // ==========================
    private Account mapToEntity(AccountDTO dto) {

        Account account = new Account();
        account.setAccountType(dto.getAccountType());
        account.setCreditLimit(dto.getCreditLimit());
        account.setCurrentBalance(dto.getCurrentBalance());

        return account;
    }
}
