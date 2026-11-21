package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name = "accounts")
@Data
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Pattern(
    //     regexp = "^10\\d{8}$",
    //     message = "Account number must start with 10 and contain exactly 10 digits"
    // )
    @Column(name = "account_number", unique = true, nullable = false, length = 10)
    private String accountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(precision = 14, scale = 2, nullable = false)
    private BigDecimal balance;

    @Column(name = "opened_on", nullable = false)
    private LocalDate openedOn;

    
}