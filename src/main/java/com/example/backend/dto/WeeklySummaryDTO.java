package com.example.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class WeeklySummaryDTO {

    private LocalDate weekStart;
    private BigDecimal totalIncome;
    private BigDecimal totalDebits;
    private BigDecimal totalEmi;
    private BigDecimal netFlow;

    public WeeklySummaryDTO(LocalDate weekStart,
                            BigDecimal totalIncome,
                            BigDecimal totalDebits,
                            BigDecimal totalEmi) {

        this.weekStart = weekStart;
        this.totalIncome = totalIncome;
        this.totalDebits = totalDebits;
        this.totalEmi = totalEmi;

        this.netFlow = totalIncome
                .subtract(totalDebits)
                .subtract(totalEmi);
    }

    public LocalDate getWeekStart() { return weekStart; }
    public BigDecimal getTotalIncome() { return totalIncome; }
    public BigDecimal getTotalDebits() { return totalDebits; }
    public BigDecimal getTotalEmi() { return totalEmi; }
    public BigDecimal getNetFlow() { return netFlow; }
}