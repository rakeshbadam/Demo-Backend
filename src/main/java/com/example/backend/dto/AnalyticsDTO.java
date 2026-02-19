package com.example.backend.dto;

import java.math.BigDecimal;

public class AnalyticsDTO {

    private Long customerId;
    private BigDecimal value;
    private String metricType; // CREDIT_UTILIZATION or DTI

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }
}
