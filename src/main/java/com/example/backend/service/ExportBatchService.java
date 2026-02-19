package com.example.backend.service;

import com.example.backend.dto.ExportBatchDTO;

import java.util.List;

public interface ExportBatchService {

    ExportBatchDTO createExportBatch(ExportBatchDTO dto);

    // helper: create default last-3-month batch
    ExportBatchDTO createLast3MonthsBatch(Long customerId);

    List<ExportBatchDTO> getAllExportBatches();

    ExportBatchDTO getExportBatchById(Long id);

    List<ExportBatchDTO> getExportBatchesByCustomerId(Long customerId);

    List<ExportBatchDTO> getPendingBatches();

    ExportBatchDTO markInProgress(Long batchId);

    ExportBatchDTO markSuccess(Long batchId, String exportFileKey, Integer rowCount);

    ExportBatchDTO markFailed(Long batchId, String errorMessage);

    void deleteExportBatch(Long id);
}
