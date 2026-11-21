package com.banksphere.controller;

import com.banksphere.dto.AccountResponse;
import com.banksphere.dto.PageResponse;
import com.banksphere.entity.AccountType;
import com.banksphere.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AccountService accountService;

    public AdminController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<AccountResponse>> getAllAccounts(
            @RequestParam(required = false) AccountType type,
            @PageableDefault(size = 5, sort = "openedOn", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {
        Page<AccountResponse> page = accountService.getAllAccounts(type, pageable, authentication);

        PageResponse<AccountResponse> response = new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );

        return ResponseEntity.ok(response);
    }
}