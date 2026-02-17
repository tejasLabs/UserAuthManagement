package com.UserAuthMicroservice.dtos;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SubscriptionRequestDTO(
    @NotNull(message = "User ID is required")
    UUID userId,

    @NotBlank(message = "Subscription type is required")
    @Pattern(regexp = "FREE|MONTHLY|YEARLY|LIFETIME", message = "Subscription type must be one of FREE, MONTHLY, YEARLY, or LIFETIME")
    String subscriptionType,
    
    @NotNull(message = "End date of subscription is required")
    @Future(message = "End date must be in the future")
    Instant endDate) {}
