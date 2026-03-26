package com.example.banking.system.controller;

import com.example.banking.system.dto.request.TransactionRequestDTO;
import com.example.banking.system.dto.response.AccountResponseDTO;
import com.example.banking.system.dto.response.TransactionResponseDTO;
import com.example.banking.system.service.ITransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private ITransactionService transactionService;

    // Customer - fund transfer
    @PostMapping("/customer/transfer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public TransactionResponseDTO transfer(@Valid @RequestBody TransactionRequestDTO request) {
        return transactionService.transfer(request);
    }

    // Customer - view own transaction history
    @GetMapping("/customer/transactions/{accountNumber}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<TransactionResponseDTO> getMyTransactions(@PathVariable String accountNumber) {
        return transactionService.getMyTransactions(accountNumber);
    }

    // Admin - view all transactions
    @GetMapping("/admin/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    // Customer and Admin - balance inquiry
    @GetMapping("/customer/balance/{accountNumber}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public AccountResponseDTO getBalance(@PathVariable String accountNumber) {
        return transactionService.getBalance(accountNumber);
    }
}