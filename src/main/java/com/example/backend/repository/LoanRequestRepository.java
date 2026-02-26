package com.example.backend.repository;

import com.example.backend.entity.LoanRequest;
import com.example.backend.enums.LoanRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {

    boolean existsByCustomerIdAndCreatedAtAfter(Long customerId, LocalDateTime date);

    List<LoanRequest> findByStatus(LoanRequestStatus status);
    @Query("""
       SELECT l FROM LoanRequest l
       WHERE (:cursor IS NULL OR l.id > :cursor)
       ORDER BY l.id ASC
       """)
    List<LoanRequest> findAfterCursor(@Param("cursor") Long cursor, Pageable pageable);

    List<LoanRequest> findByStatusOrderByIdAsc(
        LoanRequestStatus status,
        Pageable pageable
);

List<LoanRequest> findByStatusAndIdGreaterThanOrderByIdAsc(
        LoanRequestStatus status,
        Long cursor,
        Pageable pageable
);
}