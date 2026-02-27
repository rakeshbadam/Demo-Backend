package com.example.backend.service;

import com.example.backend.entity.LoanReview;
import com.example.backend.entity.LoanRequest;
import com.example.backend.pagination.CursorPage;

import java.util.List;

public interface LoanReviewService {

    /**
     * Start review for a loan request.
     * Only allowed if status = SUCCESS.
     */
    LoanReview startReview(Long loanRequestId, String reviewerName);

    /**
     * Submit decision for a loan request.
     * Decision can be APPROVED / REJECTED / ESCALATED.
     */
    LoanReview submitDecision(Long loanRequestId,
                               String reviewerName,
                               String decision,
                               String notes,
                               String escalationReason);

    /**
     * Get review history for a specific loan request.
     */
    List<LoanReview> getReviewsByLoanRequest(Long loanRequestId);

    /**
     * Get all reviews using cursor-based pagination.
     */
    CursorPage<LoanReview> getAllReviews(Long cursor, int size, String decision);

    /**
     * Get review queue (SUCCESS only) using cursor-based pagination.
     */
    CursorPage<LoanRequest> getReviewQueue(Long cursor, int size);
}