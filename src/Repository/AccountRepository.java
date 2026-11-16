package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.AccountEntity;
import com.example.demo.entity.AccountType;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    // Find account using account number
    Optional<AccountEntity> findByAccountNumber(String accountNumber);

    // Check whether account number already exists
    boolean existsByAccountNumber(String accountNumber);

    // Find all accounts belonging to a particular user
    List<AccountEntity> findByOwnerUsername(String username);

    // Find all accounts with pagination
    Page<AccountEntity> findAll(Pageable pageable);

    // Find accounts filtered by account type with pagination
    Page<AccountEntity> findByAccountType(AccountType accountType, Pageable pageable);
}