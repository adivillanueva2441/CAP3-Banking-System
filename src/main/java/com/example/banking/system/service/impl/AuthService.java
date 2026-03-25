package com.example.banking.system.service.impl;

import com.example.banking.system.config.JwtUtil;
import com.example.banking.system.dto.request.LoginRequestDTO;
import com.example.banking.system.dto.request.RegisterRequestDTO;
import com.example.banking.system.dto.response.LoginResponseDTO;
import com.example.banking.system.dto.response.RegisterResponseDTO;
import com.example.banking.system.model.Account;
import com.example.banking.system.model.User;
import com.example.banking.system.model.enums.Status;
import com.example.banking.system.repository.AccountRepository;
import com.example.banking.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        // Spring Security handles credential validation here
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");

        // If we reach here, credentials are valid
        String token = jwtUtil.generateToken(request.getUsername(), role);

        return new LoginResponseDTO(token);
    }

    public RegisterResponseDTO registerUser(RegisterRequestDTO request){
        // Add this check
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists!");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        //Automatically create a savings account for the created user.
        Account account = new Account();
        account.setUser(savedUser);
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(Status.ACTIVE);
        accountRepository.save(account);

        RegisterResponseDTO response = new RegisterResponseDTO();
        response.setId(savedUser.getId());
        response.setFirstName(savedUser.getFirstName());
        response.setMiddleName(savedUser.getMiddleName());
        response.setLastName(savedUser.getLastName());
        response.setUsername(savedUser.getUsername());
        response.setRole(savedUser.getRole());
        response.setStatus(savedUser.getStatus());
        response.setCreatedAt(savedUser.getCreatedAt());

        return response;
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            int number = (int) (Math.random() * 900000000) + 100000000;
            accountNumber = "ACC-" + number;
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}