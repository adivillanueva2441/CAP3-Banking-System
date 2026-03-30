package com.example.banking.system.service.impl;

import com.example.banking.system.dto.request.UpdateUserRequestDTO;
import com.example.banking.system.dto.response.UserResponseDTO;
import com.example.banking.system.model.User;
import com.example.banking.system.model.enums.AuditAction;
import com.example.banking.system.model.enums.Status;
import com.example.banking.system.repository.UserRepository;
import com.example.banking.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogService auditLogService;

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponseDTO::new)
                .toList();
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponseDTO(user);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UpdateUserRequestDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String oldDetails = String.format("Name changed: %s %s → %s %s",
                user.getFirstName(), user.getLastName(),
                request.getFirstName(), request.getLastName());

        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        userRepository.save(user);

        auditLogService.logSuccess(
                currentUsername(),
                AuditAction.USER_UPDATED,
                "User", id, oldDetails
        );

        return new UserResponseDTO(user);
    }

    @Override
    public UserResponseDTO deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getStatus() == Status.INACTIVE) {
            throw new RuntimeException("User is already inactive.");
        }
        user.setStatus(Status.INACTIVE);
        userRepository.save(user);

        auditLogService.logSuccess(
                currentUsername(),
                AuditAction.USER_DEACTIVATED,
                "User", id,
                String.format("User '%s' status changed: ACTIVE → INACTIVE.", user.getUsername())
        );

        return new UserResponseDTO(user);
    }

    @Override
    public UserResponseDTO restoreUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getStatus() == Status.ACTIVE) {
            throw new RuntimeException("User is already active.");
        }
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);

        auditLogService.logSuccess(
                currentUsername(),
                AuditAction.USER_RESTORED,
                "User", id,
                String.format("User '%s' status changed: INACTIVE → ACTIVE.", user.getUsername())
        );

        return new UserResponseDTO(user);
    }
}

