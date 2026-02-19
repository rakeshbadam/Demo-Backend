package com.example.backend.service;

import com.example.backend.dto.AnalyticsDTO;

public interface AnalyticsService {

    AnalyticsDTO calculateCreditUtilization(Long customerId);

    AnalyticsDTO calculateDTI(Long customerId);
}
