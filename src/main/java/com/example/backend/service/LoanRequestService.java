package com.example.backend.service;

import com.example.backend.entity.LoanRequest;
import com.example.backend.dto.UpdateLoanRequestStatusDTO;
import com.example.backend.enums.LoanRequestStatus;
import com.example.backend.pagination.CursorPage;
import java.util.List;

public interface LoanRequestService {

    LoanRequest createLoanRequest(Long customerId);

    List<LoanRequest> getPendingRequests();

    LoanRequest updateStatus(Long id, UpdateLoanRequestStatusDTO dto);

    List<LoanRequest> getRequestsByStatus(LoanRequestStatus status);

    void deleteLoanRequest(Long id);   
    CursorPage<LoanRequest> getPage(Long cursor, int size);
    List<LoanRequest> getAllRequests();
}