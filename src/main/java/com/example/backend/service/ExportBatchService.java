package com.example.backend.service;

import com.example.backend.dto.CreateExportBatchDTO;
import com.example.backend.dto.ExportBatchDTO;
import com.example.backend.dto.UpdateExportBatchStatusDTO;

import java.util.List;

public interface ExportBatchService {

    ExportBatchDTO createExportBatch(CreateExportBatchDTO dto);

    ExportBatchDTO createLast3MonthsBatch(Long customerId);

    List<ExportBatchDTO> getAllBatches();

    ExportBatchDTO getBatchById(Long batchId);

    List<ExportBatchDTO> getBatchesByCustomerId(Long customerId);

    List<ExportBatchDTO> getPendingBatches();

    ExportBatchDTO updateBatchStatus(Long batchId, UpdateExportBatchStatusDTO dto);

    void deleteBatch(Long batchId);
    
    List<Map<String, Object>> getAllCustomerAnalytics();
}
