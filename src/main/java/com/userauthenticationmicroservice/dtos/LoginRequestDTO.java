package com.userauthenticationmicroservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
    @NotBlank(message = "Please enter your email address")
    @Email(message = "Please enter a valid email address")
    @Size(max = 255, message = "Email must be less than 255 characters")
    String email,
    
    @NotBlank(message = "Please enter your password")
    String password) {}