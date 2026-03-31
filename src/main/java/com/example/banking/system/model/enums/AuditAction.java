package com.example.banking.system.model.enums;

public enum AuditAction {
    // Auth
    LOGIN_SUCCESS,
    LOGIN_FAILED_INVALID_PASSWORD,
    LOGIN_FAILED_USER_NOT_FOUND,
    LOGIN_FAILED_INACTIVE_ACCOUNT,
    LOGOUT,

    // User Management
    USER_UPDATED,
    USER_DEACTIVATED,
    USER_RESTORED,

    // Account Management
    ACCOUNT_ACTIVATED,
    ACCOUNT_DEACTIVATED
}