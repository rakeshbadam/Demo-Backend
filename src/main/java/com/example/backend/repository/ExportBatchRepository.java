package com.example.backend.repository;

import com.example.backend.entity.ExportBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportBatchRepository extends JpaRepository<ExportBatch, Long> {

    List<ExportBatch> findByStatus(String status);

    List<ExportBatch> findByCustomerCustomerId(Long customerId);
}
