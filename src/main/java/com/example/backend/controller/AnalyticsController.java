package com.example.backend.controller;

import com.example.backend.dto.AnalyticsDTO;
import com.example.backend.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    // CREDIT UTILIZATION
    @GetMapping("/customer/{customerId}/credit-utilization")
    public ResponseEntity<AnalyticsDTO> getCreditUtilization(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                analyticsService.calculateCreditUtilization(customerId)
        );
    }

    // DTI
    @GetMapping("/customer/{customerId}/dti")
    public ResponseEntity<AnalyticsDTO> getDTI(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                analyticsService.calculateDTI(customerId)
        );
    }
}
