package com.example.backend.repository;

import com.example.backend.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // 🔹 Find all accounts for a specific customer
    List<Account> findByCustomerCustomerId(Long customerId);
}
