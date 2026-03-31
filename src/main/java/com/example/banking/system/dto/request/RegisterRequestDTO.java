package com.example.banking.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Pattern(
            regexp = "^[a-zA-ZÀ-ÿ '-]+$",
            message = "First name can only contain letters, spaces, hyphens, and apostrophes"
    )
    private String firstName;

    @Size(max = 50, message = "Middle name must not exceed 50 characters")
    @Pattern(
            regexp = "^[a-zA-ZÀ-ÿ '-]*$",
            message = "Middle name can only contain letters, spaces, hyphens, and apostrophes"
    )
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Pattern(
            regexp = "^[a-zA-ZÀ-ÿ '-]+$",
            message = "Last name can only contain letters, spaces, hyphens, and apostrophes"
    )
    private String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Username can only contain letters, numbers, and underscores"
    )
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,}$",
            message = "Password must contain at least 1 uppercase letter, 1 number, and 1 special character"
    )
    private String password;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}