package com.example.banking.system.dto.response;

import com.example.banking.system.model.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountResponseDTO {

    private Long id;
    private Long userId;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String status;
    private LocalDateTime createdAt;

    //For admin to know who owns the specific account
    private String firstName;
    private String lastName;
    private String username;

    public AccountResponseDTO(Account account) {
        this.id = account.getId();
        this.userId = account.getUser().getId();
        this.accountNumber = account.getAccountNumber();
        this.accountType = account.getAccountType().name();
        this.balance = account.getBalance();
        this.status = account.getStatus().name();
        this.createdAt = account.getCreatedAt();
        this.firstName = account.getUser().getFirstName();
        this.lastName = account.getUser().getLastName();
        this.username = account.getUser().getUsername();
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

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
