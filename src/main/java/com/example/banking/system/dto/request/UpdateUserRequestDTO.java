package com.example.banking.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateUserRequestDTO {

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

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}