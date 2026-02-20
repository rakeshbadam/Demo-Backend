package com.example.backend.service;

import com.example.backend.entity.LoanRequest;
import com.example.backend.dto.UpdateLoanRequestStatusDTO;
import java.util.List;

public interface LoanRequestService {

    LoanRequest createLoanRequest(Long customerId);

    List<LoanRequest> getPendingRequests();

    LoanRequest updateStatus(Long id, UpdateLoanRequestStatusDTO dto);



}
