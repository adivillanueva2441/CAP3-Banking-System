package com.example.banking.system.aspect;

import com.example.banking.system.dto.request.LoginRequestDTO;
import com.example.banking.system.exception.InactiveAccountException;
import com.example.banking.system.model.enums.AuditAction;
import com.example.banking.system.repository.UserRepository;
import com.example.banking.system.service.impl.AuditLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserRepository userRepository;

    // Intercepts AuthService.login()
    @Around("execution(* com.example.banking.system.service.impl.AuthService.login(..))")
    public Object auditLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        // Extract username from the LoginRequestDTO argument
        LoginRequestDTO request = (LoginRequestDTO) joinPoint.getArgs()[0];
        String username = request.getUsername();

        // Check username existence BEFORE authentication runs
        boolean userExists = userRepository.findByUsername(username).isPresent();

        try {
            Object result = joinPoint.proceed();

            // Login succeeded
            auditLogService.logSuccess(
                    username,
                    AuditAction.LOGIN_SUCCESS,
                    "User",
                    null,
                    "User logged in successfully."
            );

            return result;

        } catch (InactiveAccountException ex) {
            // User exists but account is inactive
            auditLogService.logFailed(
                    username,
                    AuditAction.LOGIN_FAILED_INACTIVE_ACCOUNT,
                    "User", null,
                    "Login attempt failed: account is inactive."
            );
            // Rethrow with the clear message — frontend SHOULD see this one
            throw ex;

        } catch (Exception ex) {
            if (!userExists) {
                // Username was not in DB
                auditLogService.logFailed(
                        username,
                        AuditAction.LOGIN_FAILED_USER_NOT_FOUND,
                        "User", null,
                        "Login attempt failed: username does not exist."
                );
            } else {
                // Username exists but password was wrong
                auditLogService.logFailed(
                        username,
                        AuditAction.LOGIN_FAILED_INVALID_PASSWORD,
                        "User", null,
                        "Login attempt failed: incorrect password."
                );
            }
            // Always throw generic message to frontend
            throw new RuntimeException("Invalid credentials.");
        }
    }
}