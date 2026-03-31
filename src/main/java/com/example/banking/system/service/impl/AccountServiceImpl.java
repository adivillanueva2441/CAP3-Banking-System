package com.example.banking.system.service.impl;

import com.example.banking.system.dto.request.DepositRequestDTO;
import com.example.banking.system.dto.response.AccountResponseDTO;
import com.example.banking.system.exception.BadRequestException;
import com.example.banking.system.exception.ResourceNotFoundException;
import com.example.banking.system.exception.UnauthorizedException;
import com.example.banking.system.model.Account;
import com.example.banking.system.model.Transaction;
import com.example.banking.system.model.User;
import com.example.banking.system.model.enums.*;
import com.example.banking.system.repository.AccountRepository;
import com.example.banking.system.repository.TransactionRepository;
import com.example.banking.system.repository.UserRepository;
import com.example.banking.system.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageHandlerService messageHandler;
    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private TransactionRepository transactionRepository;

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // For admin to view all accounts
    @Override
    public List<AccountResponseDTO> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(AccountResponseDTO::new)
                .toList();
    }

    @Override
    //View specific account
    public AccountResponseDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(messageHandler.get("error.account.not_found")));
        String currentUsername = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        boolean isAdmin = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        //Ensures that users can only view their own accounts, while admin can view any account.
        if (!isAdmin && !account.getUser().getUsername().equals(currentUsername)) {
            throw new UnauthorizedException(messageHandler.get("error.unauthorized"));
        }

        return new AccountResponseDTO(account);
    }

    @Override
    //Retrieves all accounts owned by a user
    public List<AccountResponseDTO> getAllAccountsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(messageHandler.get("error.user.not_found")));

        String currentUsername = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        boolean isAdmin = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !user.getUsername().equals(currentUsername)) {
            throw new UnauthorizedException(messageHandler.get("error.unauthorized"));
        }

        List<Account> accounts = accountRepository.findByUserId(userId);

        return accounts.stream().map(AccountResponseDTO::new).toList();
    }

    //Retrieves accounts of current user after login
    @Override
    public List<AccountResponseDTO> getMyAccounts() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(messageHandler.get("error.user.not_found")));
        List<Account> accounts = accountRepository.findByUserUsername(username);
        return accounts.stream().map(AccountResponseDTO::new).toList();
    }


    //For admin to activate an account
    @Override
    public AccountResponseDTO activateAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));

        // Guard — cannot activate account if owner is deactivated
        if (account.getUser().getStatus() == Status.INACTIVE) {
            throw new BadRequestException(
                    String.format("Cannot activate account '%s' because the owner '%s' is deactivated.",
                            account.getAccountNumber(), account.getUser().getUsername())
            );
        }

        if(account.getStatus() == Status.ACTIVE) {
            throw new BadRequestException(messageHandler.get("error.account.already_active"));
        }
        account.setStatus(Status.ACTIVE);
        accountRepository.save(account);

        auditLogService.logSuccess(
                currentUsername(),
                AuditAction.ACCOUNT_ACTIVATED,
                "Account", id,
                String.format("Account '%s' status changed: INACTIVE → ACTIVE.",
                        account.getAccountNumber())
        );

        return new AccountResponseDTO(account);
    }

    //For admin to deactivate an account
    @Override
    public AccountResponseDTO deactivateAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
        if(account.getStatus() == Status.INACTIVE) {
            throw new BadRequestException(messageHandler.get("error.account.already_inactive"));
        }
        account.setStatus(Status.INACTIVE);
        accountRepository.save(account);

        auditLogService.logSuccess(
                currentUsername(),
                AuditAction.ACCOUNT_DEACTIVATED,
                "Account", id,
                String.format("Account '%s' status changed: ACTIVE → INACTIVE.",
                        account.getAccountNumber())
        );

        return new AccountResponseDTO(account);
    }

    @Override
    public AccountResponseDTO applyForSavingsAccount(){
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userRepository.findByUsername(username).
                orElseThrow(() -> new ResourceNotFoundException(messageHandler.get("error.user.not_found")));

        // Check if user already has a savings account
        boolean hasSavings = accountRepository.findByUserUsername(username)
                .stream()
                .anyMatch(acc -> acc.getAccountType() == AccountType.SAVINGS);

        if (hasSavings) {
            throw new BadRequestException(messageHandler.get("error.savings_account.already_exists"));
        }

        Account account = new Account();
        account.setUser(user);
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(AccountType.SAVINGS);
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(Status.ACTIVE);
        accountRepository.save(account);

        return new AccountResponseDTO(account);
    }

    @Override
    public AccountResponseDTO depositBalance(Long id, DepositRequestDTO depositRequestDTO){
        String username = Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getName();

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messageHandler.get("error.user.not_found")));

        if (!account.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException(messageHandler.get("error.deposit.unauthorized"));
        }

        if (account.getStatus() != Status.ACTIVE) {
            throw new BadRequestException(messageHandler.get("error.deposit.inactive"));
        }

        if (depositRequestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(messageHandler.get("error.deposit.invalid_amount"));
        }

        account.setBalance(account.getBalance().add(depositRequestDTO.getAmount()));
        accountRepository.save(account);

        // Record the deposit as a transaction
        Transaction transaction = new Transaction();
        transaction.setSenderAccount(null);
        transaction.setReceiverAccount(account);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setTransactionCost(BigDecimal.ZERO);
        transaction.setAmount(depositRequestDTO.getAmount());
        transaction.setTransactionDescription("Deposit");
        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        return new AccountResponseDTO(account);
    }


    @Override
    public String generateAccountNumber() {
        String accountNumber;
        do {
            int number = (int) (Math.random() * 900000000) + 100000000;
            accountNumber = "ACC-" + number;
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }






}
