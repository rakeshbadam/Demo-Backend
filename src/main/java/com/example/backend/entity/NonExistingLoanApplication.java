package com.example.backend.entity;

import com.example.backend.enums.LoanRequestStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "non_existing_loan_application")
public class NonExistingLoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private BigDecimal income;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanRequestStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTime;

    private LocalDateTime modifiedTime;

    @PrePersist
    protected void onCreate() {
        this.createdTime = LocalDateTime.now();
        this.modifiedTime = LocalDateTime.now();
        this.status = LoanRequestStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifiedTime = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public LoanRequestStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public LocalDateTime getModifiedTime() {
        return modifiedTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public void setStatus(LoanRequestStatus status) {
        this.status = status;
    }
}