package com.example.backend.controller;

import com.example.backend.dto.ExportBatchDTO;
import com.example.backend.service.ExportBatchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/export-batches")
public class ExportBatchController {

    @Autowired
    private ExportBatchService exportBatchService;

    // CREATE custom batch (window provided)
    @PostMapping
    public ResponseEntity<ExportBatchDTO> createExportBatch(@Valid @RequestBody ExportBatchDTO dto) {
        ExportBatchDTO created = exportBatchService.createExportBatch(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // CREATE last-3-months batch (window auto)
    @PostMapping("/customer/{customerId}/last3months")
    public ResponseEntity<ExportBatchDTO> createLast3MonthsBatch(@PathVariable Long customerId) {
        ExportBatchDTO created = exportBatchService.createLast3MonthsBatch(customerId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<ExportBatchDTO>> getAllExportBatches() {
        return ResponseEntity.ok(exportBatchService.getAllExportBatches());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ExportBatchDTO> getExportBatchById(@PathVariable Long id) {
        return ResponseEntity.ok(exportBatchService.getExportBatchById(id));
    }

    // GET BY CUSTOMER
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ExportBatchDTO>> getBatchesByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(exportBatchService.getExportBatchesByCustomerId(customerId));
    }

    // JOB: GET PENDING
    @GetMapping("/pending")
    public ResponseEntity<List<ExportBatchDTO>> getPendingBatches() {
        return ResponseEntity.ok(exportBatchService.getPendingBatches());
    }

    // JOB: MARK IN_PROGRESS
    @PutMapping("/{id}/in-progress")
    public ResponseEntity<ExportBatchDTO> markInProgress(@PathVariable Long id) {
        return ResponseEntity.ok(exportBatchService.markInProgress(id));
    }

    // JOB: MARK SUCCESS
    @PutMapping("/{id}/success")
    public ResponseEntity<ExportBatchDTO> markSuccess(
            @PathVariable Long id,
            @RequestParam String exportFileKey,
            @RequestParam Integer rowCount) {

        return ResponseEntity.ok(exportBatchService.markSuccess(id, exportFileKey, rowCount));
    }

    // JOB: MARK FAILED
    @PutMapping("/{id}/failed")
    public ResponseEntity<ExportBatchDTO> markFailed(
            @PathVariable Long id,
            @RequestParam String errorMessage) {

        return ResponseEntity.ok(exportBatchService.markFailed(id, errorMessage));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExportBatch(@PathVariable Long id) {
        exportBatchService.deleteExportBatch(id);
        return ResponseEntity.ok("Export batch deleted successfully");
    }
}
