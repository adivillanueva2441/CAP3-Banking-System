package com.example.banking.system.controller;

import com.example.banking.system.dto.AccountResponseDto;
import com.example.banking.system.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    private IAccountService accountService;

    //API call to retrieve all accounts of all users
    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    //API call to retrieve information of a specific account
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    //API call to retrieve all accounts of a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResponseDto>> getAllAccountsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAllAccountsByUserId(userId));
    }

    //API call to activate an account
    @PatchMapping("/{id}/activate")
    public ResponseEntity<AccountResponseDto> activateAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.activateAccount(id));
    }

    //API call to deactivate an account
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<AccountResponseDto> deactivateAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.deactivateAccount(id));
    }
}
