package com.banksphere.dto;

import com.banksphere.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;
    private LocalDateTime createdAt;
}
