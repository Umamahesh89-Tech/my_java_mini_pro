package com.banksphere.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false, length = 10)
    private String accountNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal balance;

    @Column(name = "opened_on", nullable = false)
    private LocalDate openedOn;

    public Account() {
    }

    public Account(Long id, String accountNumber, User owner, AccountType accountType, BigDecimal balance, LocalDate openedOn) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.accountType = accountType;
        this.balance = balance;
        this.openedOn = openedOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDate getOpenedOn() {
        return openedOn;
    }

    public void setOpenedOn(LocalDate openedOn) {
        this.openedOn = openedOn;
    }
}