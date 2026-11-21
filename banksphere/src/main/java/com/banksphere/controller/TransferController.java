package com.banksphere.controller;

import com.banksphere.dto.TransferRequest;
import com.banksphere.dto.TransferResponse;
import com.banksphere.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransactionService transactionService;

    public TransferController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TransferResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {
        TransferResponse response = transactionService.transfer(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}