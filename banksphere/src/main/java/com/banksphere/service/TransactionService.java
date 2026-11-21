package com.banksphere.service;

import com.banksphere.dto.*;
import com.banksphere.entity.Account;
import com.banksphere.entity.Transaction;
import com.banksphere.entity.TransactionType;
import com.banksphere.exception.BadRequestException;
import com.banksphere.repository.AccountRepository;
import com.banksphere.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionResponse deposit(String accountNumber, AmountRequest request, Authentication authentication) {
        Account account = accountService.getOwnedAccount(accountNumber, authentication);
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);
        return addTransaction(account, TransactionType.DEPOSIT, request.getAmount(), "Deposit");
    }

    @Transactional
    public TransactionResponse withdraw(String accountNumber, AmountRequest request, Authentication authentication) {
        Account account = accountService.getOwnedAccount(accountNumber, authentication);
        BigDecimal next = account.getBalance().subtract(request.getAmount());
        if (next.compareTo(account.getAccountType().getMinimumBalance()) < 0) {
            throw new BadRequestException("Withdrawal would violate minimum balance");
        }
        account.setBalance(next);
        accountRepository.save(account);
        return addTransaction(account, TransactionType.WITHDRAWAL, request.getAmount(), "Withdrawal");
    }

    @Transactional
    public TransferResponse transfer(TransferRequest request, Authentication authentication) {
        Account source = accountService.getOwnedAccount(request.getFromAccountNumber(), authentication);
        Account destination = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new BadRequestException("Destination account not found"));
        if (source.getAccountNumber().equals(destination.getAccountNumber())) {
            throw new BadRequestException("Cannot transfer to the same account");
        }
        BigDecimal next = source.getBalance().subtract(request.getAmount());
        if (next.compareTo(source.getAccountType().getMinimumBalance()) < 0) {
            throw new BadRequestException("Transfer would violate minimum balance");
        }
        source.setBalance(next);
        destination.setBalance(destination.getBalance().add(request.getAmount()));
        accountRepository.save(source);
        accountRepository.save(destination);
        addTransaction(source, TransactionType.TRANSFER_OUT, request.getAmount(), "Transfer to " + destination.getAccountNumber());
        addTransaction(destination, TransactionType.TRANSFER_IN, request.getAmount(), "Transfer from " + source.getAccountNumber());
        return new TransferResponse("Transfer successful", source.getAccountNumber(), destination.getAccountNumber(), request.getAmount());
    }

    public PageResponse<TransactionResponse> history(String accountNumber, Pageable pageable, Authentication authentication) {
        Account account = accountService.getOwnedAccount(accountNumber, authentication);
        Page<Transaction> page = transactionRepository.findByAccountId(account.getId(), pageable);
        return new PageResponse<>(page.map(this::toResponse).getContent(), page.getNumber(),
                page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    public MiniStatementResponse getMiniStatement(String accountNumber, Authentication authentication) {
        Account account = accountService.getOwnedAccount(accountNumber, authentication);
        java.util.List<TransactionResponse> transactions =
                transactionRepository.findTop5ByAccountIdOrderByCreatedAtDesc(account.getId())
                        .stream().map(this::toResponse).toList();
        return new MiniStatementResponse(account.getAccountNumber(), account.getAccountType(),
                account.getBalance(), transactions);
    }

    public PageResponse<TransactionResponse> getTransactionHistory(
            String accountNumber, Pageable pageable, Authentication authentication) {
        return history(accountNumber, pageable, authentication);
    }

    private TransactionResponse addTransaction(Account account, TransactionType type,
                                                BigDecimal amount, String description) {
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setType(type);
        tx.setAmount(amount);
        tx.setBalanceAfter(account.getBalance());
        tx.setNote(description);
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(tx);
        return toResponse(tx);
    }

    private TransactionResponse toResponse(Transaction tx) {
        return new TransactionResponse(tx.getId(), tx.getType(), tx.getAmount(),
                tx.getBalanceAfter(), tx.getNote(), tx.getCreatedAt());
    }
}
