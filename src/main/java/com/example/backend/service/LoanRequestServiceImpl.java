package com.example.backend.service;

import com.example.backend.dto.UpdateLoanRequestStatusDTO;
import com.example.backend.entity.LoanRequest;
import com.example.backend.enums.LoanRequestStatus;
import com.example.backend.pagination.AbstractCursorService;
import com.example.backend.pagination.CursorPage;
import com.example.backend.repository.LoanRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanRequestServiceImpl
    extends AbstractCursorService<LoanRequest, LoanRequest>
    implements LoanRequestService {

    @Autowired
    private LoanRequestRepository loanRequestRepository;

    @Override
    public LoanRequest createLoanRequest(Long customerId) {
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
    public List<LoanRequest> getRequestsByStatus(LoanRequestStatus status) {
        return loanRequestRepository.findByStatus(status);
    }

    @Override
    public LoanRequest updateStatus(Long id, UpdateLoanRequestStatusDTO dto) {
        LoanRequest request = loanRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan request not found"));

        request.setStatus(dto.getStatus());
        request.setFilePath(dto.getFilePath());
        request.setProcessedAt(LocalDateTime.now());

        return loanRequestRepository.save(request);
    }

    @Override
    public void deleteLoanRequest(Long id) {
        LoanRequest request = loanRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan request not found"));

        loanRequestRepository.delete(request);
    }

    @Override
    protected List<LoanRequest> fetchAfterCursor(Long cursor, Pageable pageable) {
        return loanRequestRepository.findAfterCursor(cursor, pageable);
    }

    @Override
    protected Long extractId(LoanRequest entity) {
        return entity.getId();
    }

    @Override
    protected LoanRequest mapToDTO(LoanRequest entity) {
        return entity;
    }

    @Override
    public CursorPage<LoanRequest> getPage(Long cursor, int size) {
        return super.getPage(cursor, size);
    }
    @Override
public List<LoanRequest> getAllRequests() {
    return loanRequestRepository.findAll();
}
}
