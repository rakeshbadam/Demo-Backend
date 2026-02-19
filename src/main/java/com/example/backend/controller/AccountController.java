package com.example.backend.controller;

import com.example.backend.dto.AccountDTO;
import com.example.backend.dto.CustomerAccountOverviewDTO;
import com.example.backend.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // ==========================
    // CREATE ACCOUNT
    // ==========================
    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(
            @Valid @RequestBody AccountDTO accountDTO) {

        AccountDTO createdAccount = accountService.createAccount(accountDTO);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    // ==========================
    // GET ALL ACCOUNTS
    // ==========================
    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    // ==========================
    // GET ACCOUNT BY ID
    // ==========================
    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    // ==========================
    // GET ACCOUNTS BY CUSTOMER ID
    // ==========================
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountDTO>> getAccountsByCustomer(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                accountService.getAccountsByCustomerId(customerId)
        );
    }

    // ==========================
    // CUSTOMER ACCOUNT OVERVIEW
    // ==========================
    @GetMapping("/customer/{customerId}/overview")
    public ResponseEntity<CustomerAccountOverviewDTO> getCustomerAccountsOverview(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                accountService.getCustomerAccountsOverview(customerId)
        );
    }

    // ==========================
    // UPDATE ACCOUNT
    // ==========================
    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountDTO accountDTO) {

        return ResponseEntity.ok(
                accountService.updateAccount(id, accountDTO)
        );
    }

    // ==========================
    // DELETE ACCOUNT
    // ==========================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {

        accountService.deleteAccount(id);
        return ResponseEntity.ok("Account deleted successfully");
    }
}
