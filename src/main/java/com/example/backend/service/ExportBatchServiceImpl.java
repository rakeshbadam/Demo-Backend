package com.example.backend.service;

import com.example.backend.dto.ExportBatchDTO;
import com.example.backend.entity.Customer;
import com.example.backend.entity.ExportBatch;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.ExportBatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportBatchServiceImpl implements ExportBatchService {

    @Autowired
    private ExportBatchRepository exportBatchRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public ExportBatchDTO createExportBatch(ExportBatchDTO dto) {

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId())
                );

        ExportBatch batch = mapToEntity(dto);
        batch.setCustomer(customer);

        if (batch.getStatus() == null || batch.getStatus().isBlank()) {
            batch.setStatus("PENDING");
        }
        if (batch.getAttemptCount() == null) {
            batch.setAttemptCount(0);
        }

        ExportBatch saved = exportBatchRepository.save(batch);
        return mapToDTO(saved);
    }

    @Override
    public ExportBatchDTO createLast3MonthsBatch(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + customerId)
                );

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusMonths(3);

        ExportBatch batch = new ExportBatch();
        batch.setCustomer(customer);
        batch.setWindowStart(start);
        batch.setWindowEnd(end);
        batch.setStatus("PENDING");
        batch.setAttemptCount(0);

        ExportBatch saved = exportBatchRepository.save(batch);
        return mapToDTO(saved);
    }

    @Override
    public List<ExportBatchDTO> getAllExportBatches() {
        return exportBatchRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExportBatchDTO getExportBatchById(Long id) {
        ExportBatch batch = exportBatchRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Export batch not found with id: " + id)
                );
        return mapToDTO(batch);
    }

    @Override
    public List<ExportBatchDTO> getExportBatchesByCustomerId(Long customerId) {

        customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + customerId)
                );

        return exportBatchRepository.findByCustomerCustomerId(customerId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExportBatchDTO> getPendingBatches() {
        return exportBatchRepository.findByStatusOrderByCreatedTimeAsc("PENDING")
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExportBatchDTO markInProgress(Long batchId) {

        ExportBatch batch = exportBatchRepository.findById(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Export batch not found with id: " + batchId)
                );

        batch.setStatus("IN_PROGRESS");
        batch.setAttemptCount((batch.getAttemptCount() == null ? 0 : batch.getAttemptCount()) + 1);
        batch.setLastAttemptTime(LocalDateTime.now());
        batch.setErrorMessage(null);

        ExportBatch saved = exportBatchRepository.save(batch);
        return mapToDTO(saved);
    }

    @Override
    public ExportBatchDTO markSuccess(Long batchId, String exportFileKey, Integer rowCount) {

        ExportBatch batch = exportBatchRepository.findById(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Export batch not found with id: " + batchId)
                );

        batch.setStatus("SUCCESS");
        batch.setExportFileKey(exportFileKey);
        batch.setRowCount(rowCount);
        batch.setErrorMessage(null);

        ExportBatch saved = exportBatchRepository.save(batch);
        return mapToDTO(saved);
    }

    @Override
    public ExportBatchDTO markFailed(Long batchId, String errorMessage) {

        ExportBatch batch = exportBatchRepository.findById(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Export batch not found with id: " + batchId)
                );

        batch.setStatus("FAILED");
        batch.setErrorMessage(errorMessage);
        batch.setLastAttemptTime(LocalDateTime.now());

        ExportBatch saved = exportBatchRepository.save(batch);
        return mapToDTO(saved);
    }

    @Override
    public void deleteExportBatch(Long id) {
        if (!exportBatchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Export batch not found with id: " + id);
        }
        exportBatchRepository.deleteById(id);
    }

    // ============
    // ENTITY → DTO
    // ============
    private ExportBatchDTO mapToDTO(ExportBatch batch) {
        ExportBatchDTO dto = new ExportBatchDTO();
        dto.setBatchId(batch.getBatchId());
        dto.setCustomerId(batch.getCustomer().getCustomerId());
        dto.setWindowStart(batch.getWindowStart());
        dto.setWindowEnd(batch.getWindowEnd());
        dto.setStatus(batch.getStatus());
        dto.setAttemptCount(batch.getAttemptCount());
        dto.setLastAttemptTime(batch.getLastAttemptTime());
        dto.setErrorMessage(batch.getErrorMessage());
        dto.setExportFileKey(batch.getExportFileKey());
        dto.setRowCount(batch.getRowCount());
        dto.setCreatedTime(batch.getCreatedTime());
        dto.setModifiedTime(batch.getModifiedTime());
        return dto;
    }

    // ============
    // DTO → ENTITY
    // ============
    private ExportBatch mapToEntity(ExportBatchDTO dto) {
        ExportBatch batch = new ExportBatch();
        batch.setWindowStart(dto.getWindowStart());
        batch.setWindowEnd(dto.getWindowEnd());
        batch.setStatus(dto.getStatus());
        batch.setAttemptCount(dto.getAttemptCount());
        batch.setLastAttemptTime(dto.getLastAttemptTime());
        batch.setErrorMessage(dto.getErrorMessage());
        batch.setExportFileKey(dto.getExportFileKey());
        batch.setRowCount(dto.getRowCount());
        return batch;
    }
}
