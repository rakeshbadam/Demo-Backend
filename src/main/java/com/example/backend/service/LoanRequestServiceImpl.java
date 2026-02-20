package com.example.backend.service;

import com.example.backend.entity.LoanRequest;
import com.example.backend.enums.LoanRequestStatus;
import com.example.backend.dto.UpdateLoanRequestStatusDTO;
import com.example.backend.repository.LoanRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class LoanRequestServiceImpl implements LoanRequestService {

    @Autowired
    private LoanRequestRepository loanRequestRepository;

    @Override
    public LoanRequest createLoanRequest(Long customerId) {

        // 🔹 Check 3-month restriction
        boolean exists = loanRequestRepository
                .existsByCustomerIdAndCreatedAtAfter(
                        customerId,
                        LocalDateTime.now().minusMonths(3)
                );

        if (exists) {
            throw new RuntimeException(
                "You already submitted a loan request in last 3 months."
            );
        }

        LoanRequest request = new LoanRequest();
        request.setCustomerId(customerId);
        request.setStartDate(LocalDateTime.now().minusMonths(3));
        request.setEndDate(LocalDateTime.now());
        request.setStatus(LoanRequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        return loanRequestRepository.save(request);
    }

    @Override
public List<LoanRequest> getPendingRequests() {
    return loanRequestRepository.findByStatus(LoanRequestStatus.PENDING);
}

@Override
public LoanRequest updateStatus(Long id, UpdateLoanRequestStatusDTO dto) {

    LoanRequest request = loanRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Loan request not found"));

    request.setStatus(dto.getStatus());
    request.setFilePath(dto.getFilePath());
    request.setProcessedAt(java.time.LocalDateTime.now());

    return loanRequestRepository.save(request);
}
}
