package com.example.banking.system.controller;

import com.example.banking.system.dto.request.LoginRequestDTO;
import com.example.banking.system.dto.request.RegisterRequestDTO;
import com.example.banking.system.dto.response.LoginResponseDTO;
import com.example.banking.system.dto.response.RegisterResponseDTO;
import com.example.banking.system.model.enums.AuditAction;
import com.example.banking.system.service.impl.AuditLogService;
import com.example.banking.system.service.impl.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuditLogService auditLogService;

    public AuthController(AuthService authService, AuditLogService auditLogService) {
        this.authService = authService;
        this.auditLogService = auditLogService;
    }

    @PostMapping("/register")
    public RegisterResponseDTO register(@Valid @RequestBody RegisterRequestDTO request) {
        return authService.registerUser(request);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public String logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        auditLogService.logSuccess(username, AuditAction.LOGOUT, "User", null, "User logged out.");
        return ("Logged out successfully.");
    }
}
