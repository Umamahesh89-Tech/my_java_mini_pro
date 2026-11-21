package com.banksphere.repository;

import com.banksphere.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Used for the mini-statement: gets the last five lines, newest first
    List<Transaction> findTop5ByAccountIdOrderByCreatedAtDesc(Long accountId);
    
    // Used for the full paged transaction history
    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);
}