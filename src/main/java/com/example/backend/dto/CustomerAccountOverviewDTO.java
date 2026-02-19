package com.example.backend.dto;

import java.util.List;

public class CustomerAccountOverviewDTO {

    private Long customerId;
    private int totalAccounts;
    private List<AccountSummaryDTO> accounts;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public int getTotalAccounts() {
        return totalAccounts;
    }

    public void setTotalAccounts(int totalAccounts) {
        this.totalAccounts = totalAccounts;
    }

    public List<AccountSummaryDTO> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountSummaryDTO> accounts) {
        this.accounts = accounts;
    }
}
