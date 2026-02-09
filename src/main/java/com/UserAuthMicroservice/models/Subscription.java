package com.UserAuthMicroservice.models;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Subscription extends BaseModel {

    @NonNull
    @OneToMany(mappedBy = "subscription")
    private User user;

    @NonNull
    private SubscriptionType subscriptionType = SubscriptionType.FREE;
    
    @NonNull
    @Column(nullable=false)
    private Instant startDate;

    private Instant endDate = null;

}
