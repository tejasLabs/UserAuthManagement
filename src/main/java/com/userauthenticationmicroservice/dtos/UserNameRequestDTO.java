package com.userauthenticationmicroservice.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserNameRequestDTO(
    @NotNull(message = "User ID is required")
    UUID userId,

    @NotBlank
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters long")
    @Pattern(regexp = "^[\\p{L}0-9._ ]+$", message = "Username can only contain letters, numbers, spaces, dots, and underscores")
    String username
) {}
