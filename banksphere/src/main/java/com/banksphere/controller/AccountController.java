package com.banksphere.controller;

import com.banksphere.dto.AccountResponse;
import com.banksphere.dto.AmountRequest;
import com.banksphere.dto.MiniStatementResponse;
import com.banksphere.dto.OpenAccountRequest;
import com.banksphere.dto.PageResponse;
import com.banksphere.dto.TransactionResponse;
import com.banksphere.service.AccountService;
import com.banksphere.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    public AccountController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AccountResponse> openAccount(
            @Valid @RequestBody OpenAccountRequest request,
            Authentication authentication) {
        AccountResponse response = accountService.openAccount(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<AccountResponse>> getMyAccounts(Authentication authentication) {
        return ResponseEntity.ok(accountService.getMyAccounts(authentication));
    }

    @GetMapping("/{accountNumber}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<AccountResponse> getAccountByNumber(
            @PathVariable String accountNumber,
            Authentication authentication) {
        return ResponseEntity.ok(accountService.getAccountByNumber(accountNumber, authentication));
    }

    @PostMapping("/{accountNumber}/deposit")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TransactionResponse> deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody AmountRequest request,
            Authentication authentication) {
        TransactionResponse response = transactionService.deposit(accountNumber, request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{accountNumber}/withdraw")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TransactionResponse> withdraw(
            @PathVariable String accountNumber,
            @Valid @RequestBody AmountRequest request,
            Authentication authentication) {
        TransactionResponse response = transactionService.withdraw(accountNumber, request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{accountNumber}/mini-statement")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<MiniStatementResponse> getMiniStatement(
            @PathVariable String accountNumber,
            Authentication authentication) {
        return ResponseEntity.ok(transactionService.getMiniStatement(accountNumber, authentication));
    }

    @GetMapping("/{accountNumber}/transactions")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<PageResponse<TransactionResponse>> getTransactionHistory(
            @PathVariable String accountNumber,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {
        return ResponseEntity.ok(transactionService.getTransactionHistory(accountNumber, pageable, authentication));
    }
}