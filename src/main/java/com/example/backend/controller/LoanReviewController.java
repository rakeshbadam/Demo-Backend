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

    @PostMapping("/{loanRequestId}/start")
    public ResponseEntity<?> startReview(@PathVariable Long loanRequestId,
                                         @RequestParam String reviewerName) {
        return ResponseEntity.ok(
                reviewService.startReview(loanRequestId, reviewerName)
        );
    }

    @PostMapping("/{loanRequestId}/decision")
    public ResponseEntity<?> submitDecision(@PathVariable Long loanRequestId,
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

    @GetMapping("/{loanRequestId}")
    public ResponseEntity<?> getReviews(@PathVariable Long loanRequestId) {
        return ResponseEntity.ok(
                reviewService.getReviewsByLoanRequest(loanRequestId)
        );
    }

    @GetMapping
    public ResponseEntity<?> getAllReviews(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                reviewService.getAllReviews(cursor, size)
        );
    }

   @GetMapping("/queue")
public ResponseEntity<?> getReviewQueue(
        @RequestParam(required = false) Long cursor,
        @RequestParam(defaultValue = "5") int size) {

    return ResponseEntity.ok(
            reviewService.getReviewQueue(cursor, size)
    );
}
}