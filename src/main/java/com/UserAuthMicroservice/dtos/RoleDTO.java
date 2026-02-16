package com.UserAuthMicroservice.dtos;

import jakarta.validation.constraints.NotBlank;


public record RoleDTO(
    @NotBlank(message = "Role value is required")
    String value
) {}
