package com.banksphere.dto;

import com.banksphere.entity.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AccountResponse {
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private LocalDate openedAt;
}
