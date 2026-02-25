package com.example.backend.service;

import com.example.backend.entity.NonExistingLoanApplication;
import com.example.backend.enums.LoanRequestStatus;
import com.example.backend.pagination.CursorPage;

import java.util.List;

public interface NonExistingLoanApplicationService {

    NonExistingLoanApplication createApplication(NonExistingLoanApplication application);

    List<NonExistingLoanApplication> getPendingApplications();
    List<NonExistingLoanApplication> getByStatus(LoanRequestStatus status);

    NonExistingLoanApplication updateStatus(Long id, LoanRequestStatus status);
    CursorPage<NonExistingLoanApplication> getPage(Long cursor, int size);
    void deleteApplication(Long id);
}