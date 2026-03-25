package com.example.banking.system.service;

import com.example.banking.system.dto.response.AccountResponseDTO;

import java.util.List;

public interface IAccountService {
    List<AccountResponseDTO> getAllAccounts();
    AccountResponseDTO getAccountById(Long id);
    List<AccountResponseDTO> getAllAccountsByUserId(Long id);
    AccountResponseDTO activateAccount(Long id);
    AccountResponseDTO deactivateAccount(Long id);

}
