package com.example.backend.repository;

import com.example.backend.entity.LoanReview;
import com.example.backend.enums.LoanRequestStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanReviewRepository extends JpaRepository<LoanReview, Long> {

    // Get all reviews for a specific loan request
    List<LoanReview> findByLoanRequestIdOrderByIdAsc(Long loanRequestId);

    // ========================
    // Existing Cursor Pagination
    // ========================

    List<LoanReview> findByIdGreaterThanOrderByIdAsc(Long id, Pageable pageable);

    List<LoanReview> findAllByOrderByIdAsc(Pageable pageable);

    // ========================
    // Decision Filtering
    // ========================

    List<LoanReview> findByDecisionOrderByIdAsc(
            LoanRequestStatus decision,
            Pageable pageable
    );

    List<LoanReview> findByDecisionAndIdGreaterThanOrderByIdAsc(
            LoanRequestStatus decision,
            Long id,
            Pageable pageable
    );
}