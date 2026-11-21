package com.banksphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransferResponse {
    private String message;
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
}
