package com.example.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyTotalsDTO {

    private LocalDate date;
    private BigDecimal totalCredits;
    private BigDecimal totalDebits;
    private BigDecimal totalEmi;
    private BigDecimal netFlow;

    public DailyTotalsDTO(LocalDate date,
                          BigDecimal totalCredits,
                          BigDecimal totalDebits,
                          BigDecimal totalEmi) {

        this.date = date;
        this.totalCredits = totalCredits;
        this.totalDebits = totalDebits;
        this.totalEmi = totalEmi;

        this.netFlow = totalCredits
                .subtract(totalDebits)
                .subtract(totalEmi);
    }

    public LocalDate getDate() { return date; }
    public BigDecimal getTotalCredits() { return totalCredits; }
    public BigDecimal getTotalDebits() { return totalDebits; }
    public BigDecimal getTotalEmi() { return totalEmi; }
    public BigDecimal getNetFlow() { return netFlow; }
}