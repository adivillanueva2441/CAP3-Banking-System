package com.example.banking.system.service.impl;

import com.example.banking.system.dto.request.TransactionRequestDTO;
import com.example.banking.system.dto.response.AccountResponseDTO;
import com.example.banking.system.dto.response.TransactionResponseDTO;
import com.example.banking.system.exception.BadRequestException;
import com.example.banking.system.exception.ResourceNotFoundException;
import com.example.banking.system.exception.UnauthorizedException;
import com.example.banking.system.model.Account;
import com.example.banking.system.model.Transaction;
import com.example.banking.system.model.enums.Status;
import com.example.banking.system.model.enums.TransactionStatus;
import com.example.banking.system.model.enums.TransactionType;
import com.example.banking.system.repository.AccountRepository;
import com.example.banking.system.repository.TransactionRepository;
import com.example.banking.system.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransactionServiceImpl implements ITransactionService {

    // Flat fee — easy to refactor into admin-configurable later
    private static final BigDecimal TRANSACTION_FEE = new BigDecimal("15.00");

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MessageHandlerService messageHandlerService;

    @Override
    @Transactional
    public TransactionResponseDTO transfer(TransactionRequestDTO request) {
        String currentUsername = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        // Fetch sender and receiver accounts
        Account senderAccount = accountRepository.findByAccountNumber(request.getSenderAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(messageHandlerService.get("error.sender_account.not_found")));

        Account receiverAccount = accountRepository.findByAccountNumber(request.getReceiverAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(messageHandlerService.get("error.receiver_account.not_found")));

        // Ensure sender owns this account
        if (!senderAccount.getUser().getUsername().equals(currentUsername)) {
            throw new UnauthorizedException(messageHandlerService.get("error.unauthorized"));
        }

        // Ensure both accounts are active
        if (senderAccount.getStatus() != Status.ACTIVE) {
            throw new BadRequestException(messageHandlerService.get("error.sender_account.inactive"));
        }
        if (receiverAccount.getStatus() != Status.ACTIVE) {
            throw new BadRequestException(messageHandlerService.get("error.receiver_account.inactive"));
        }

        // Ensure sender is not transferring to themselves
        if (senderAccount.getAccountNumber().equals(receiverAccount.getAccountNumber())) {
            throw new BadRequestException(messageHandlerService.get("error.transfer.same_account"));
        }

        BigDecimal totalDeducted = request.getAmount().add(TRANSACTION_FEE);

        // Ensure sender has enough balance
        if (senderAccount.getBalance().compareTo(totalDeducted) < 0) {
            throw new BadRequestException(messageHandlerService.get("error.insufficient_balance"));
        }

        // Build transaction record first as PENDING
        Transaction transaction = new Transaction();
        transaction.setSenderAccount(senderAccount);
        transaction.setReceiverAccount(receiverAccount);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setTransactionCost(TRANSACTION_FEE);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDescription(request.getTransactionDescription());
        transaction.setStatus(TransactionStatus.PENDING);

        try {
            // Deduct from sender
            senderAccount.setBalance(senderAccount.getBalance().subtract(totalDeducted));

            // Credit to receiver
            receiverAccount.setBalance(receiverAccount.getBalance().add(request.getAmount()));

            // Save updated balances
            accountRepository.save(senderAccount);
            accountRepository.save(receiverAccount);

            // Mark as completed
            transaction.setStatus(TransactionStatus.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new RuntimeException("Transfer failed: " + e.getMessage());
        }

        transactionRepository.save(transaction);
        return new TransactionResponseDTO(transaction);
    }

    @Override
    public List<TransactionResponseDTO> getMyTransactions(String accountNumber) {
        String currentUsername = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException(messageHandlerService.get("error.account.not_found")));

        // Ensure the account belongs to the current user
        if (!account.getUser().getUsername().equals(currentUsername)) {
            throw new UnauthorizedException(messageHandlerService.get("error.unauthorized"));
        }

        // Fetch both sent and received transactions then combine
        List<TransactionResponseDTO> sent = transactionRepository
                .findBySenderAccountId(account.getId())
                .stream()
                .map(TransactionResponseDTO::new)
                .toList();

        List<TransactionResponseDTO> received = transactionRepository
                .findByReceiverAccountId(account.getId())
                .stream()
                .map(TransactionResponseDTO::new)
                .toList();

        return Stream.concat(sent.stream(), received.stream())
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponseDTO> getAllTransactions() {
        // Admin only — enforced at controller level via @PreAuthorize
        return transactionRepository.findAll()
                .stream()
                .map(TransactionResponseDTO::new)
                .toList();
    }

    @Override
    public AccountResponseDTO getBalance(String accountNumber) {
        String currentUsername = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        boolean isAdmin = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException(messageHandlerService.get("error.account.not_found")));

        if (!isAdmin && !account.getUser().getUsername().equals(currentUsername)) {
            throw new UnauthorizedException(messageHandlerService.get("error.unauthorized"));
        }

        return new AccountResponseDTO(account);
    }
}