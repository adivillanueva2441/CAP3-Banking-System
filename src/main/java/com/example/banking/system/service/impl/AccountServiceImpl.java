package com.example.banking.system.service.impl;

import com.example.banking.system.dto.response.AccountResponseDTO;
import com.example.banking.system.model.Account;
import com.example.banking.system.model.User;
import com.example.banking.system.model.enums.Status;
import com.example.banking.system.repository.AccountRepository;
import com.example.banking.system.repository.UserRepository;
import com.example.banking.system.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

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
        Account account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
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
            throw new RuntimeException("You are not authorized to view this account");
        }

        return new AccountResponseDTO(account);
    }

    @Override
    //Retrieves all accounts owned by a user
    public List<AccountResponseDTO> getAllAccountsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String currentUsername = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        boolean isAdmin = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !user.getUsername().equals(currentUsername)) {
            throw new RuntimeException("You are not authorized to view these accounts");
        }

        List<Account> accounts = accountRepository.findByUserId(userId);

        return accounts.stream().map(AccountResponseDTO::new).toList();
    }


    //For admin to activate an account
    @Override
    public AccountResponseDTO activateAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
        if(account.getStatus() == Status.ACTIVE) {
            throw new RuntimeException("Account is already active.");
        }
        account.setStatus(Status.ACTIVE);
        accountRepository.save(account);
        return new AccountResponseDTO(account);
    }

    //For admin to deactivate an account
    @Override
    public AccountResponseDTO deactivateAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
        if(account.getStatus() == Status.INACTIVE) {
            throw new RuntimeException("Account is already inactive.");
        }
        account.setStatus(Status.INACTIVE);
        accountRepository.save(account);
        return new AccountResponseDTO(account);
    }






}
