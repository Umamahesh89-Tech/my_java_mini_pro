package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.TransactionEntity;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    // Get the latest 5 transactions for an account
    List<TransactionEntity> findTop5ByAccountIdOrderByCreatedAtDesc(Long accountId);

    // Get transaction history with pagination
    Page<TransactionEntity> findByAccountId(Long accountId, Pageable pageable);
}