package com.example.banking.system.dto.response;

import com.example.banking.system.model.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponseDTO {

    private Long id;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private String transactionType;
    private BigDecimal transactionCost;
    private BigDecimal amount;
    private BigDecimal totalDeducted;
    private String status;
    private String transactionDescription;
    private LocalDateTime createdAt;

    public TransactionResponseDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.senderAccountNumber = transaction.getSenderAccount().getAccountNumber();
        this.receiverAccountNumber = transaction.getReceiverAccount().getAccountNumber();
        this.transactionType = transaction.getTransactionType().name();
        this.transactionCost = transaction.getTransactionCost();
        this.amount = transaction.getAmount();
        this.totalDeducted = transaction.getAmount().add(transaction.getTransactionCost());
        this.status = transaction.getStatus().name();
        this.transactionDescription = transaction.getTransactionDescription();
        this.createdAt = transaction.getCreatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSenderAccountNumber() { return senderAccountNumber; }
    public void setSenderAccountNumber(String senderAccountNumber) { this.senderAccountNumber = senderAccountNumber; }

    public String getReceiverAccountNumber() { return receiverAccountNumber; }
    public void setReceiverAccountNumber(String receiverAccountNumber) { this.receiverAccountNumber = receiverAccountNumber; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public BigDecimal getTransactionCost() { return transactionCost; }
    public void setTransactionCost(BigDecimal transactionCost) { this.transactionCost = transactionCost; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getTotalDeducted() { return totalDeducted; }
    public void setTotalDeducted(BigDecimal totalDeducted) { this.totalDeducted = totalDeducted; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTransactionDescription() { return transactionDescription; }
    public void setTransactionDescription(String transactionDescription) { this.transactionDescription = transactionDescription; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}