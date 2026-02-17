package com.userauthenticationmicroservice.dtos;

import java.util.Set;
import java.util.UUID;


public record UserResponseDTO (
    UUID id,
    String username,
    String email,
    Set<String> roles
) {}
