package com.example.backend.controller;

import com.example.backend.dto.UpdateLoanRequestStatusDTO;
import com.example.backend.entity.LoanRequest;
import com.example.backend.enums.LoanRequestStatus;
import com.example.backend.pagination.CursorPage;
import com.example.backend.service.LoanRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loan-request")
public class LoanRequestController {

    @Autowired
    private LoanRequestService loanRequestService;

    @PostMapping("/{customerId}")
    public LoanRequest createLoanRequest(@PathVariable Long customerId) {
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

   //FLEXIBLE ENDPOINT
    @GetMapping("/status/{status}")
    public List<LoanRequest> getRequestsByStatus(
            @PathVariable LoanRequestStatus status) {

        return loanRequestService.getRequestsByStatus(status);
    }
    @DeleteMapping("/{id}")
     public String deleteLoanRequest(@PathVariable Long id) {

    loanRequestService.deleteLoanRequest(id);

    return "Loan request deleted successfully with id: " + id;
    }
    @GetMapping("/cursor")
    public ResponseEntity<CursorPage<LoanRequest>> getCursor(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                loanRequestService.getPage(cursor, size)
        );
    }

    @GetMapping
    public List<LoanRequest> getAllRequests() {
        return loanRequestService.getAllRequests();
    }
}