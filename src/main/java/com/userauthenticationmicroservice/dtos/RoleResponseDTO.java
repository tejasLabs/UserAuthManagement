package com.userauthenticationmicroservice.dtos;

import java.util.Set;
import java.util.UUID;

public record RoleResponseDTO(UUID userId, Set<String> roles) {
}