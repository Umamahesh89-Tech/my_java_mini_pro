package com.banksphere.repository;

import com.banksphere.entity.Account;
import com.banksphere.entity.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    // Used for every account endpoint using the account number
    Optional<Account> findByAccountNumber(String accountNumber);
    
    // Used when generating a fresh random 10-digit account number to ensure uniqueness
    boolean existsByAccountNumber(String accountNumber);
    
    // Used for the "my accounts" endpoint (GET /api/accounts/me)
    // Spring Data automatically traverses Account.owner -> User.username
    List<Account> findByOwnerUsername(String username);
    
    // Inherited findAll(Pageable pageable) is used for the admin account list
    
    // Used for the admin account list filtered by type (?type=SAVINGS or CURRENT)
    Page<Account> findByAccountType(AccountType accountType, Pageable pageable);
}