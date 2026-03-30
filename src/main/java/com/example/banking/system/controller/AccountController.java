package com.example.banking.system.controller;

import com.example.banking.system.dto.response.AccountResponseDTO;
import com.example.banking.system.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    private IAccountService accountService;

    //API call for ADMIN to retrieve all accounts of all users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    //API call to retrieve information of a specific account
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    //API call to retrieve all accounts of a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResponseDTO>> getAllAccountsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAllAccountsByUserId(userId));
    }

    //API call to retrieve accounts of current user
    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<AccountResponseDTO>> getMyAccounts() {
        return ResponseEntity.ok(accountService.getMyAccounts());
    }

    //API call for ADMIN to activate an account
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<AccountResponseDTO> activateAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.activateAccount(id));
    }

    //API call for ADMIN to deactivate an account
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<AccountResponseDTO> deactivateAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.deactivateAccount(id));
    }

    @PostMapping("/apply-savings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AccountResponseDTO> applyForSavingsAccount() {
        return ResponseEntity.ok(accountService.applyForSavingsAccount());
    }
}
