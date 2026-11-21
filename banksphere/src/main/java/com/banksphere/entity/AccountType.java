package com.banksphere.entity;

import java.math.BigDecimal;

public enum AccountType {
    SAVINGS(new BigDecimal("1000.00")),
    CURRENT(new BigDecimal("5000.00"));

    private final BigDecimal minimumBalance;

    AccountType(BigDecimal minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }
}