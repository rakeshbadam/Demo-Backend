package com.example.backend.repository;

import com.example.backend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("""
           SELECT c FROM Customer c
           WHERE (:cursor IS NULL OR c.customerId > :cursor)
           ORDER BY c.customerId ASC
           """)
    List<Customer> findAfterCursor(@Param("cursor") Long cursor, Pageable pageable);
}
