package com.example.backend.repository;

import com.example.backend.entity.NonExistingLoanApplication;
import com.example.backend.enums.LoanRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NonExistingLoanApplicationRepository
        extends JpaRepository<NonExistingLoanApplication, Long> {

    List<NonExistingLoanApplication> findByStatus(LoanRequestStatus status);
}