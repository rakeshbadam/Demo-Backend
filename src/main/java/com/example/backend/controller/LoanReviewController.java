package com.example.backend.controller;

import com.example.backend.service.LoanReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loan-review")
public class LoanReviewController {

    private final LoanReviewService reviewService;

    public LoanReviewController(LoanReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // ===============================
    // START REVIEW
    // ===============================
    @PostMapping("/{loanRequestId}/start")
    public ResponseEntity<?> startReview(
            @PathVariable Long loanRequestId,
            @RequestParam String reviewerName) {

        return ResponseEntity.ok(
                reviewService.startReview(loanRequestId, reviewerName)
        );
    }

    // ===============================
    // SUBMIT DECISION
    // ===============================
    @PostMapping("/{loanRequestId}/decision")
    public ResponseEntity<?> submitDecision(
            @PathVariable Long loanRequestId,
            @RequestParam String reviewerName,
            @RequestParam String decision,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) String escalationReason) {

        return ResponseEntity.ok(
                reviewService.submitDecision(
                        loanRequestId,
                        reviewerName,
                        decision,
                        notes,
                        escalationReason
                )
        );
    }

    // ===============================
    // GET REVIEWS BY LOAN REQUEST
    // ===============================
    @GetMapping("/{loanRequestId}")
    public ResponseEntity<?> getReviews(@PathVariable Long loanRequestId) {
        return ResponseEntity.ok(
                reviewService.getReviewsByLoanRequest(loanRequestId)
        );
    }

    // ===============================
    // GET ALL REVIEWS (CURSOR + FILTER)
    // ===============================
    @GetMapping
    public ResponseEntity<?> getAllReviews(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String decision) {

        return ResponseEntity.ok(
                reviewService.getAllReviews(cursor, size, decision)
        );
    }

    // ===============================
    // REVIEW QUEUE (UNDER_REVIEW)
    // ===============================
    @GetMapping("/queue")
    public ResponseEntity<?> getReviewQueue(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(
                reviewService.getReviewQueue(cursor, size)
        );
    }
}