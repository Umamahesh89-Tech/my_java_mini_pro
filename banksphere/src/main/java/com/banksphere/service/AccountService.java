package com.banksphere.service;

import com.banksphere.dto.AccountResponse;
import com.banksphere.dto.OpenAccountRequest;
import com.banksphere.entity.Account;
import com.banksphere.entity.User;
import com.banksphere.exception.BadRequestException;
import com.banksphere.exception.ResourceNotFoundException;
import com.banksphere.repository.AccountRepository;
import com.banksphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public AccountResponse openAccount(OpenAccountRequest request, Authentication authentication) {
        User owner = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        BigDecimal deposit = request.getInitialDeposit() == null ? BigDecimal.ZERO : request.getInitialDeposit();
        if (deposit.compareTo(request.getAccountType().getMinimumBalance()) < 0) {
            throw new BadRequestException("Initial deposit is below the minimum balance");
        }

        Account account = new Account();
        account.setOwner(owner);
        account.setAccountType(request.getAccountType());
        account.setBalance(deposit);
        account.setOpenedOn(LocalDate.now());
        account.setAccountNumber(generateAccountNumber());
        accountRepository.save(account);
        return toResponse(account);
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = Long.toString(ThreadLocalRandom.current()
                    .nextLong(1_000_000_000L, 10_000_000_000L));
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    public List<AccountResponse> getMyAccounts(Authentication authentication) {
        return accountRepository.findByOwnerUsername(authentication.getName()).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public AccountResponse getAccountByNumber(String accountNumber, Authentication authentication) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !account.getOwner().getUsername().equals(authentication.getName())) {
            throw new ResourceNotFoundException("Account not found");
        }
        return toResponse(account);
    }

    public Page<AccountResponse> getAllAccounts(
            com.banksphere.entity.AccountType accountType, Pageable pageable, Authentication authentication) {
        Page<Account> accounts = accountType == null
                ? accountRepository.findAll(pageable)
                : accountRepository.findByAccountType(accountType, pageable);
        return accounts.map(this::toResponse);
    }

    public Account getOwnedAccount(String accountNumber, Authentication authentication) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        if (!account.getOwner().getUsername().equals(authentication.getName())) {
            throw new ResourceNotFoundException("Account not found");
        }
        return account;
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(account.getAccountNumber(), account.getAccountType(),
                account.getBalance(), account.getOpenedOn());
    }
}
