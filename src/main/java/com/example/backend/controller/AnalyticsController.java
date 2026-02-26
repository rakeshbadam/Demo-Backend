package com.example.backend.controller;

import com.example.backend.dto.AnalyticsDTO;
import com.example.backend.dto.DailyTotalsDTO;
import com.example.backend.dto.WeeklySummaryDTO;
import com.example.backend.service.AnalyticsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    // CREDIT UTILIZATION (Single Customer)
    @GetMapping("/customer/{customerId}/credit-utilization")
    public ResponseEntity<AnalyticsDTO> getCreditUtilization(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                analyticsService.calculateCreditUtilization(customerId)
        );
    }

    // DTI (Single Customer)
    @GetMapping("/customer/{customerId}/dti")
    public ResponseEntity<AnalyticsDTO> getDTI(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                analyticsService.calculateDTI(customerId)
        );
    }

    // ALL CUSTOMERS DTI
    @GetMapping("/customers/dti")
    public ResponseEntity<List<AnalyticsDTO>> getAllCustomersDTI() {

        return ResponseEntity.ok(
                analyticsService.calculateAllCustomersDTI()
        );
    }

    // ALL CUSTOMERS CREDIT UTILIZATION
    @GetMapping("/customers/credit-utilization")
    public ResponseEntity<List<AnalyticsDTO>> getAllCustomersCreditUtilization() {

        return ResponseEntity.ok(
                analyticsService.calculateAllCustomersCreditUtilization()
        );
    }

    // DAILY TOTALS
    @GetMapping("/customer/{customerId}/daily-totals")
    public ResponseEntity<List<DailyTotalsDTO>> getDailyTotals(     
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                analyticsService.getDailyTotals(customerId)
        );
    }

    // WEEKLY SUMMARY
    @GetMapping("/customer/{customerId}/weekly-summary")
    public ResponseEntity<List<WeeklySummaryDTO>> getWeeklySummary(     
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                analyticsService.getWeeklySummary(customerId)
        );
    }
}