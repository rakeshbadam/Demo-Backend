package com.example.backend.dto;

import com.example.backend.enums.LoanRequestStatus;


public class UpdateLoanRequestStatusDTO {

    private LoanRequestStatus status;
    private String filePath;

    public LoanRequestStatus getStatus() {
        return status;
    }

    public void setStatus(LoanRequestStatus status) {
        this.status = status;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
