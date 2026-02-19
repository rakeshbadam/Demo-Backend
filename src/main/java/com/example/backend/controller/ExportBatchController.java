package com.example.backend.controller;

import com.example.backend.dto.CreateExportBatchDTO;
import com.example.backend.dto.ExportBatchDTO;
import com.example.backend.dto.UpdateExportBatchStatusDTO;
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

    // ==========================
    // POST - Create batch (custom window)
    // ==========================
    @PostMapping
    public ResponseEntity<ExportBatchDTO> createExportBatch(
            @Valid @RequestBody CreateExportBatchDTO dto) {

        ExportBatchDTO created = exportBatchService.createExportBatch(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ==========================
    // POST - Create last 3 months batch
    // ==========================
    @PostMapping("/customer/{customerId}/last3months")
    public ResponseEntity<ExportBatchDTO> createLast3MonthsBatch(
            @PathVariable Long customerId) {

        ExportBatchDTO created = exportBatchService.createLast3MonthsBatch(customerId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ==========================
    // GET - All batches
    // ==========================
    @GetMapping
    public ResponseEntity<List<ExportBatchDTO>> getAllBatches() {
        return ResponseEntity.ok(exportBatchService.getAllBatches());
    }

    // ==========================
    // GET - By batch id
    // ==========================
    @GetMapping("/{batchId}")
    public ResponseEntity<ExportBatchDTO> getBatchById(@PathVariable Long batchId) {
        return ResponseEntity.ok(exportBatchService.getBatchById(batchId));
    }

    // ==========================
    // GET - Batches by customer
    // ==========================
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ExportBatchDTO>> getBatchesByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(exportBatchService.getBatchesByCustomerId(customerId));
    }

    // ==========================
    // GET - Pending batches (Lambda calls)
    // ==========================
    @GetMapping("/pending")
    public ResponseEntity<List<ExportBatchDTO>> getPendingBatches() {
        return ResponseEntity.ok(exportBatchService.getPendingBatches());
    }

    // ==========================
    // PUT - Update batch status (Lambda calls)
    // ==========================
    @PutMapping("/{batchId}/status")
    public ResponseEntity<ExportBatchDTO> updateBatchStatus(
            @PathVariable Long batchId,
            @Valid @RequestBody UpdateExportBatchStatusDTO dto) {

        return ResponseEntity.ok(exportBatchService.updateBatchStatus(batchId, dto));
    }

    // ==========================
    // DELETE - Delete batch
    // ==========================
    @DeleteMapping("/{batchId}")
    public ResponseEntity<String> deleteBatch(@PathVariable Long batchId) {
        exportBatchService.deleteBatch(batchId);
        return ResponseEntity.ok("Export batch deleted successfully");
    }
}
