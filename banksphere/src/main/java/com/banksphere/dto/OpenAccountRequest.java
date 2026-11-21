package com.banksphere.dto;

import com.banksphere.entity.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OpenAccountRequest {
    @NotNull private AccountType accountType;
    private BigDecimal initialDeposit;
}
