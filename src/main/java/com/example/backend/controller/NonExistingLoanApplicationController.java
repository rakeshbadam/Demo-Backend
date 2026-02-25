package com.example.backend.controller;

import com.example.backend.entity.NonExistingLoanApplication;
import com.example.backend.enums.LoanRequestStatus;
import com.example.backend.service.NonExistingLoanApplicationService;
import org.springframework.http.ResponseEntity;
import com.example.backend.pagination.CursorPage;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/non-existing-loan-applications")
public class NonExistingLoanApplicationController {

    private final NonExistingLoanApplicationService service;

    public NonExistingLoanApplicationController(
            NonExistingLoanApplicationService service) {
        this.service = service;
    }

    // 1️⃣ Create new non-existing loan application
    @PostMapping
    public ResponseEntity<NonExistingLoanApplication> createApplication(
            @RequestBody NonExistingLoanApplication application) {

        return ResponseEntity.ok(service.createApplication(application));
    }

    // 2️⃣ Get all PENDING applications (Lambda uses this)
    @GetMapping("/pending")
    public ResponseEntity<List<NonExistingLoanApplication>> getPendingApplications() {
        return ResponseEntity.ok(service.getPendingApplications());
    }

    // 3️⃣ Get by ANY status (NEW)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<NonExistingLoanApplication>> getByStatus(
            @PathVariable LoanRequestStatus status) {

        return ResponseEntity.ok(service.getByStatus(status));
    }

    // 4️⃣ Update status (Lambda uses this)
    @PutMapping("/{id}/status")
    public ResponseEntity<NonExistingLoanApplication> updateStatus(
            @PathVariable Long id,
            @RequestParam LoanRequestStatus status) {

        return ResponseEntity.ok(service.updateStatus(id, status));
    }
    @DeleteMapping("/{id}")
public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {

    service.deleteApplication(id);
    return ResponseEntity.noContent().build();
}
@GetMapping("/cursor")
public ResponseEntity<CursorPage<NonExistingLoanApplication>> getCursor(
        @RequestParam(required = false) Long cursor,
        @RequestParam(defaultValue = "10") int size) {

    return ResponseEntity.ok(
            service.getPage(cursor, size)
    );
}
}