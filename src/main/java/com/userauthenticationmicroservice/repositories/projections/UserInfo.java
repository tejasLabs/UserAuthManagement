package com.userauthenticationmicroservice.repositories.projections;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import com.userauthenticationmicroservice.models.Status;
import com.userauthenticationmicroservice.models.SubscriptionType;

public interface UserInfo {
    String getEmail();
    String getUsername();
    Status getStatus();

    //Nested Projection to fetch the role names 
    Set<RolesInfo> getRoles();

    interface RolesInfo {
        String getValue();
    }

    //nested projection to fetch subscription details
    SubscriptionInfo getSubscription();

    interface SubscriptionInfo{
        UUID getId();
        SubscriptionType getSubscriptionType();
        Status getStatus();
        Instant getStartDate();
        Instant getEndDate();
    }   
}
