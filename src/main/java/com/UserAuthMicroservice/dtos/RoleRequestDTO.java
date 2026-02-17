package com.UserAuthMicroservice.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public record RoleRequestDTO(
    @NotBlank(message = "User ID is required")
    UUID userId,

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "MODERATOR|GUEST", message = "Role must be MODERATOR or GUEST")
    String role
) {}
