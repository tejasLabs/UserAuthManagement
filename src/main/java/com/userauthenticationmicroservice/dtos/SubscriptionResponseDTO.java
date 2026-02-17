package com.userauthenticationmicroservice.dtos;

import java.time.Instant;

public record SubscriptionResponseDTO(
    String subscriptionTypeString,
    String subscriptionStatusString,
    Instant subscriptionStartDate,
    Instant subscriptionEndDate) {}
