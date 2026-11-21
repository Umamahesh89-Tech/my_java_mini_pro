package com.banksphere.dto;

import com.banksphere.entity.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class MiniStatementResponse {
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private List<TransactionResponse> transactions;
}
