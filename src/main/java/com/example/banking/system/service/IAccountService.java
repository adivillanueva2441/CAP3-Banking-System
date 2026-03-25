package com.example.banking.system.service;

import com.example.banking.system.dto.AccountResponseDto;

import java.util.List;

public interface IAccountService {
    List<AccountResponseDto> getAllAccounts();
    AccountResponseDto getAccountById(Long id);
    List<AccountResponseDto> getAllAccountsByUserId(Long id);
    AccountResponseDto activateAccount(Long id);
    AccountResponseDto deactivateAccount(Long id);

}
