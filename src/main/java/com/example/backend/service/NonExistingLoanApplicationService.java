package com.example.backend.service;

import com.example.backend.entity.NonExistingLoanApplication;
import com.example.backend.enums.LoanRequestStatus;

import java.util.List;

public interface NonExistingLoanApplicationService {

    NonExistingLoanApplication createApplication(NonExistingLoanApplication application);

    List<NonExistingLoanApplication> getPendingApplications();
    List<NonExistingLoanApplication> getByStatus(LoanRequestStatus status);

    NonExistingLoanApplication updateStatus(Long id, LoanRequestStatus status);
    void deleteApplication(Long id);
}