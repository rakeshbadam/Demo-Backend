package com.example.backend.service.impl;

import com.example.backend.entity.LoanReview;
import com.example.backend.entity.LoanRequest;
import com.example.backend.enums.LoanRequestStatus;
import com.example.backend.pagination.CursorPage;
import com.example.backend.repository.LoanReviewRepository;
import com.example.backend.repository.LoanRequestRepository;
import com.example.backend.service.LoanReviewService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LoanReviewServiceImpl implements LoanReviewService {

    private final LoanReviewRepository reviewRepository;
    private final LoanRequestRepository loanRequestRepository;

    public LoanReviewServiceImpl(LoanReviewRepository reviewRepository,
                                 LoanRequestRepository loanRequestRepository) {
        this.reviewRepository = reviewRepository;
        this.loanRequestRepository = loanRequestRepository;
    }

    // ===============================
    // START REVIEW
    // ===============================
    @Override
    public LoanReview startReview(Long loanRequestId, String reviewerName) {

        LoanRequest loanRequest = loanRequestRepository.findById(loanRequestId)
                .orElseThrow(() -> new RuntimeException("LoanRequest not found"));

        if (!loanRequest.getStatus().equals(LoanRequestStatus.SUCCESS)) {
            throw new RuntimeException("Only SUCCESS loans can be reviewed");
        }

        loanRequest.setStatus(LoanRequestStatus.UNDER_REVIEW);

        LoanReview review = new LoanReview();
        review.setLoanRequest(loanRequest);
        review.setReviewerName(reviewerName);
        review.setDecision(LoanRequestStatus.UNDER_REVIEW); // enum-safe

        return reviewRepository.save(review);
    }

    // ===============================
    // SUBMIT DECISION
    // ===============================
    @Override
    public LoanReview submitDecision(Long loanRequestId,
                                     String reviewerName,
                                     String decisionStr,
                                     String notes,
                                     String escalationReason) {

        LoanRequest loanRequest = loanRequestRepository.findById(loanRequestId)
                .orElseThrow(() -> new RuntimeException("LoanRequest not found"));

        LoanRequestStatus decision;

        try {
            decision = LoanRequestStatus.valueOf(decisionStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid decision value");
        }

        if (!(decision == LoanRequestStatus.APPROVED
                || decision == LoanRequestStatus.REJECTED
                || decision == LoanRequestStatus.ESCALATED
                || decision == LoanRequestStatus.ADDITIONAL_DOC_REQUIRED)) {
            throw new RuntimeException("Decision not allowed");
        }

        LoanReview review = new LoanReview();
        review.setLoanRequest(loanRequest);
        review.setReviewerName(reviewerName);
        review.setDecision(decision);
        review.setNotes(notes);
        review.setEscalationReason(escalationReason);

        // Update loan lifecycle status
        loanRequest.setStatus(decision);

        return reviewRepository.save(review);
    }

    // ===============================
    // GET REVIEWS BY LOAN REQUEST
    // ===============================
    @Override
    public List<LoanReview> getReviewsByLoanRequest(Long loanRequestId) {
        return reviewRepository.findByLoanRequestIdOrderByIdAsc(loanRequestId);
    }

    // ===============================
    // GET ALL REVIEWS (CURSOR + FILTER)
    // ===============================
   @Override
public CursorPage<LoanReview> getAllReviews(Long cursor,
                                            int size,
                                            String decisionStr) {

    if (size <= 0) {
        size = 5;
    }

    PageRequest pageable = PageRequest.of(0, size);

    List<LoanReview> reviews;

    LoanRequestStatus decision = null;

    // ✅ SAFE ENUM HANDLING
    if (decisionStr != null && !decisionStr.isBlank()) {
        try {
            decision = LoanRequestStatus.valueOf(decisionStr.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid decision value: " + decisionStr);
        }
    }

    if (decision != null) {
        reviews = (cursor == null)
                ? reviewRepository.findByDecisionOrderByIdAsc(decision, pageable)
                : reviewRepository.findByDecisionAndIdGreaterThanOrderByIdAsc(
                        decision, cursor, pageable);
    } else {
        reviews = (cursor == null)
                ? reviewRepository.findAllByOrderByIdAsc(pageable)
                : reviewRepository.findByIdGreaterThanOrderByIdAsc(cursor, pageable);
    }

    boolean hasNext = reviews.size() == size;
    Long nextCursor = hasNext ? reviews.get(reviews.size() - 1).getId() : null;

    return new CursorPage<>(reviews, nextCursor, hasNext);
}
    // ===============================
    // REVIEW QUEUE (UNDER_REVIEW)
    // ===============================
    @Override
    public CursorPage<LoanRequest> getReviewQueue(Long cursor, int size) {

        if (size <= 0) {
            size = 5;
        }

        PageRequest pageable = PageRequest.of(0, size);

        List<LoanRequest> requests =
                (cursor == null)
                        ? loanRequestRepository.findByStatusOrderByIdAsc(
                                LoanRequestStatus.UNDER_REVIEW, pageable)
                        : loanRequestRepository.findByStatusAndIdGreaterThanOrderByIdAsc(
                                LoanRequestStatus.UNDER_REVIEW, cursor, pageable);

        boolean hasNext = requests.size() == size;
        Long nextCursor = hasNext ? requests.get(requests.size() - 1).getId() : null;

        return new CursorPage<>(requests, nextCursor, hasNext);
    }
}