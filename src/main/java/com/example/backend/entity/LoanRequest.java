package com.example.backend.entity;

import com.example.backend.enums.LoanRequestStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan_request")
public class LoanRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private LoanRequestStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    private String filePath;

    /*
     🔥 FIXED: Prevent infinite JSON recursion
     */
    @JsonManagedReference
        @OneToMany(mappedBy = "loanRequest",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
        private List<LoanReview> loanReviews = new ArrayList<>();

    // =========================
    // GETTERS & SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LoanRequestStatus getStatus() {
        return status;
    }

    public void setStatus(LoanRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<LoanReview> getLoanReviews() {
        return loanReviews;
    }

    public void setLoanReviews(List<LoanReview> loanReviews) {
        this.loanReviews = loanReviews;

        if (loanReviews != null) {
            loanReviews.forEach(review -> review.setLoanRequest(this));
        }
    }

    public void addLoanReview(LoanReview loanReview) {
        if (loanReview == null) {
            return;
        }

        loanReviews.add(loanReview);
        loanReview.setLoanRequest(this);
    }

    // =========================
    // CONSTRUCTORS
    // =========================

    public LoanRequest() {}

    public LoanRequest(Long customerId,
                       LocalDateTime startDate,
                       LocalDateTime endDate,
                       LoanRequestStatus status,
                       LocalDateTime createdAt,
                       String filePath) {
        this.customerId = customerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdAt = createdAt;
        this.filePath = filePath;
    }
}