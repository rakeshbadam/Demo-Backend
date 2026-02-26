package com.example.backend.repository;

import com.example.backend.entity.LoanReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanReviewRepository extends JpaRepository<LoanReview, Long> {

    // Get all reviews for a specific loan request
    List<LoanReview> findByLoanRequestIdOrderByIdAsc(Long loanRequestId);

    // Cursor pagination - get next set after given id
    List<LoanReview> findByIdGreaterThanOrderByIdAsc(Long id, Pageable pageable);

    // First page of cursor pagination
    List<LoanReview> findAllByOrderByIdAsc(Pageable pageable);
}