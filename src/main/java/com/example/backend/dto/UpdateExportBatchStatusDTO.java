package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateExportBatchStatusDTO {

    @NotBlank(message = "Status is required")
    private String status; // PROCESSING, COMPLETED, FAILED

    private String filePath;     // set when COMPLETED
    private String errorMessage; // set when FAILED

    // Getters and Setters

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
