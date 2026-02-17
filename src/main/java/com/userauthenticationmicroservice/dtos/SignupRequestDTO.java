package com.userauthenticationmicroservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequestDTO(
        @NotBlank @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Pattern(regexp = "^[\\p{L}0-9._ ]+$", message = "Username can only contain letters, numbers, spaces, dots, and underscores")
        String username,

        @NotBlank @Email(message = "Please enter a valid email address")
        String email,

        @NotBlank @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters long")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
        String password) {}