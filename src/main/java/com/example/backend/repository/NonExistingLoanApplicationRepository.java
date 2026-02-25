package com.example.backend.repository;

import com.example.backend.entity.NonExistingLoanApplication;
import com.example.backend.enums.LoanRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


import java.util.List;

public interface NonExistingLoanApplicationRepository
        extends JpaRepository<NonExistingLoanApplication, Long> {

    List<NonExistingLoanApplication> findByStatus(LoanRequestStatus status);
   @Query("""
       SELECT n FROM NonExistingLoanApplication n
       WHERE (:cursor IS NULL OR n.id > :cursor)
       ORDER BY n.id ASC
       """)
List<NonExistingLoanApplication> findAfterCursor(
        @Param("cursor") Long cursor,
        Pageable pageable
);
        }