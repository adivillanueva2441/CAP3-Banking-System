package com.example.banking.system.service;

import com.example.banking.system.dto.request.TransactionRequestDTO;
import com.example.banking.system.dto.response.AccountResponseDTO;
import com.example.banking.system.dto.response.TransactionResponseDTO;

import java.util.List;

public interface ITransactionService {
    TransactionResponseDTO transfer(TransactionRequestDTO request);
    List<TransactionResponseDTO> getMyTransactions(String accountNumber);
    List<TransactionResponseDTO> getAllTransactions();
    AccountResponseDTO getBalance(String accountNumber);
}