package com.userauthenticationmicroservice.models;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Subscription extends BaseModel {

    @OneToOne(mappedBy = "subscription")
    private User user;

    private SubscriptionType subscriptionType = SubscriptionType.FREE;
    
    @Column(nullable=false)
    private Instant startDate;

    private Instant endDate = Instant.now();

}
