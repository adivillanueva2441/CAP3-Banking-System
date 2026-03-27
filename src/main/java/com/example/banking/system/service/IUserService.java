package com.example.banking.system.service;

import com.example.banking.system.dto.request.UpdateUserRequestDTO;
import com.example.banking.system.dto.response.UserResponseDTO;

import java.util.List;

public interface IUserService {
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    UserResponseDTO updateUser(Long id, UpdateUserRequestDTO request);
    UserResponseDTO deactivateUser(Long id);
    UserResponseDTO restoreUser(Long id);
}
