package com.example.backend.repository;

import com.example.backend.entity.LoanRequest;
import com.example.backend.enums.LoanRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {

    boolean existsByCustomerIdAndCreatedAtAfter(Long customerId, LocalDateTime date);

    List<LoanRequest> findByStatus(LoanRequestStatus status);
}