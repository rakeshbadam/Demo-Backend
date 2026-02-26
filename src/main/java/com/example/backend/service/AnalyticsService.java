package com.example.backend.service;

import com.example.backend.dto.AnalyticsDTO;
import com.example.backend.dto.DailyTotalsDTO;
import com.example.backend.dto.WeeklySummaryDTO;
import java.util.List;

public interface AnalyticsService {

    AnalyticsDTO calculateCreditUtilization(Long customerId);

    AnalyticsDTO calculateDTI(Long customerId);

    List<AnalyticsDTO> calculateAllCustomersDTI();

    List<AnalyticsDTO> calculateAllCustomersCreditUtilization();

    List<DailyTotalsDTO> getDailyTotals(Long customerId);

    List<WeeklySummaryDTO> getWeeklySummary(Long customerId);
}