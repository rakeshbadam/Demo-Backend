package com.example.backend.entity;

import com.example.backend.enums.LoanRequestStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getCustomerId() {
        return customerId;
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


    public LoanRequest() {
    }

    public LoanRequest(Long customerId, LocalDateTime startDate, LocalDateTime endDate, LoanRequestStatus status, LocalDateTime createdAt, String filePath) {
        this.customerId = customerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdAt = createdAt;
        this.filePath = filePath;
    }

    

}
