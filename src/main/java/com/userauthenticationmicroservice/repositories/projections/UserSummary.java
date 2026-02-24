package com.userauthenticationmicroservice.repositories.projections;

import java.time.Instant;
import java.util.UUID;

import com.userauthenticationmicroservice.models.Status;



public interface UserSummary{
    UUID getId();
    String getEmail();
    String getUsername();
    Status getStatus();
    Instant getCreatedAt();
}   