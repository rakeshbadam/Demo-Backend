package com.example.backend.controller;

import com.example.backend.entity.LoanRequest;
import com.example.backend.service.LoanRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.backend.dto.UpdateLoanRequestStatusDTO;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/loan-request")
public class LoanRequestController {

    @Autowired
    private LoanRequestService loanRequestService;

    @PostMapping("/{customerId}")
    public LoanRequest createLoanRequest(
            @PathVariable Long customerId) {

        return loanRequestService.createLoanRequest(customerId);
    }

    @PutMapping("/{id}/status")
public LoanRequest updateStatus(
        @PathVariable Long id,
        @RequestBody UpdateLoanRequestStatusDTO dto) {

    return loanRequestService.updateStatus(id, dto);
}

@GetMapping("/pending")
public List<LoanRequest> getPendingRequests() {
    return loanRequestService.getPendingRequests();
}


}
