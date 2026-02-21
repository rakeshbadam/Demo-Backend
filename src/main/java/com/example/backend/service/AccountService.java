package com.example.backend.service;

import com.example.backend.dto.AccountDTO;
import com.example.backend.dto.CustomerAccountOverviewDTO;

import java.util.List;

public interface AccountService {

    AccountDTO createAccount(AccountDTO accountDTO);

    List<AccountDTO> getAllAccounts();

    AccountDTO getAccountById(Long id);

    List<AccountDTO> getAccountsByCustomerId(Long customerId);

    
    CustomerAccountOverviewDTO getCustomerAccountsOverview(Long customerId);

    AccountDTO updateAccount(Long id, AccountDTO accountDTO);

    void deleteAccount(Long id);
}
