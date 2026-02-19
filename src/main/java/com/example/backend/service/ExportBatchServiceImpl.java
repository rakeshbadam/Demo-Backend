package com.example.backend.service;

import com.example.backend.dto.CreateExportBatchDTO;
import com.example.backend.dto.ExportBatchDTO;
import com.example.backend.dto.UpdateExportBatchStatusDTO;
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

    // ==========================
    // CREATE (custom window)
    // ==========================
    @Override
    public ExportBatchDTO createExportBatch(CreateExportBatchDTO dto) {

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId())
                );

        ExportBatch batch = new ExportBatch();
        batch.setCustomer(customer);
        batch.setStartDate(dto.getStartDate());
        batch.setEndDate(dto.getEndDate());
        batch.setStatus("PENDING");

        ExportBatch saved = exportBatchRepository.save(batch);
        return mapToDTO(saved);
    }

    // ==========================
    // CREATE (last 3 months)
    // ==========================
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
        batch.setStartDate(start);
        batch.setEndDate(end);
        batch.setStatus("PENDING");

        ExportBatch saved = exportBatchRepository.save(batch);
        return mapToDTO(saved);
    }

    // ==========================
    // GET ALL
    // ==========================
    @Override
    public List<ExportBatchDTO> getAllBatches() {
        return exportBatchRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ==========================
    // GET BY ID
    // ==========================
    @Override
    public ExportBatchDTO getBatchById(Long batchId) {

        ExportBatch batch = exportBatchRepository.findById(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Export batch not found with id: " + batchId)
                );

        return mapToDTO(batch);
    }

    // ==========================
    // GET BY CUSTOMER ID
    // ==========================
    @Override
    public List<ExportBatchDTO> getBatchesByCustomerId(Long customerId) {

        customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + customerId)
                );

        return exportBatchRepository.findByCustomerCustomerId(customerId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ==========================
    // GET PENDING (Lambda uses)
    // ==========================
    @Override
    public List<ExportBatchDTO> getPendingBatches() {

        return exportBatchRepository.findByStatus("PENDING")
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ==========================
    // UPDATE STATUS (Lambda uses)
    // ==========================
    @Override
    public ExportBatchDTO updateBatchStatus(Long batchId, UpdateExportBatchStatusDTO dto) {

        ExportBatch batch = exportBatchRepository.findById(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Export batch not found with id: " + batchId)
                );

        batch.setStatus(dto.getStatus());
        batch.setFilePath(dto.getFilePath());
        batch.setErrorMessage(dto.getErrorMessage());

        ExportBatch updated = exportBatchRepository.save(batch);
        return mapToDTO(updated);
    }

    // ==========================
    // DELETE
    // ==========================
    @Override
    public void deleteBatch(Long batchId) {

        if (!exportBatchRepository.existsById(batchId)) {
            throw new ResourceNotFoundException("Export batch not found with id: " + batchId);
        }

        exportBatchRepository.deleteById(batchId);
    }

    // ==========================
    // ENTITY → DTO
    // ==========================
    private ExportBatchDTO mapToDTO(ExportBatch batch) {

        ExportBatchDTO dto = new ExportBatchDTO();
        dto.setBatchId(batch.getBatchId());
        dto.setCustomerId(batch.getCustomer().getCustomerId());
        dto.setStartDate(batch.getStartDate());
        dto.setEndDate(batch.getEndDate());
        dto.setStatus(batch.getStatus());
        dto.setFilePath(batch.getFilePath());
        dto.setErrorMessage(batch.getErrorMessage());
        dto.setCreatedTime(batch.getCreatedTime());
        dto.setModifiedTime(batch.getModifiedTime());

        return dto;
    }
}
