package com.example.banking.system.service.impl;

import com.example.banking.system.dto.AccountResponseDto;
import com.example.banking.system.model.Account;
import com.example.banking.system.model.enums.Status;
import com.example.banking.system.repository.AccountRepository;
import com.example.banking.system.repository.UserRepository;
import com.example.banking.system.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    // For admin to view all accounts
    @Override
    public List<AccountResponseDto> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(AccountResponseDto::new)
                .toList();
    }

    @Override
    //View specific account
    public AccountResponseDto getAccountById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));

        return new AccountResponseDto(account);
    }

    @Override
    //Retrieves all accounts owned by a user
    public List<AccountResponseDto> getAllAccountsByUserId(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Account> accounts = accountRepository.findByUserId(userId);

        return accounts.stream().map(AccountResponseDto::new).toList();
    }


    //For admin to activate an account
    @Override
    public AccountResponseDto activateAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
        if(account.getStatus() == Status.ACTIVE) {
            throw new RuntimeException("Account is already active.");
        }
        account.setStatus(Status.ACTIVE);
        accountRepository.save(account);
        return new AccountResponseDto(account);
    }

    //For admin to deactivate an account
    @Override
    public AccountResponseDto deactivateAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
        if(account.getStatus() == Status.INACTIVE) {
            throw new RuntimeException("Account is already inactive.");
        }
        account.setStatus(Status.INACTIVE);
        accountRepository.save(account);
        return new AccountResponseDto(account);
    }






}
