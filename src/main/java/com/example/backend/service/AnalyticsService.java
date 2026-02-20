package com.example.backend.service;

import com.example.backend.dto.AnalyticsDTO;
import java.util.List;

public interface AnalyticsService {

    AnalyticsDTO calculateCreditUtilization(Long customerId);

    AnalyticsDTO calculateDTI(Long customerId);

    List<AnalyticsDTO> calculateAllCustomersDTI();

    List<AnalyticsDTO> calculateAllCustomersCreditUtilization();
}