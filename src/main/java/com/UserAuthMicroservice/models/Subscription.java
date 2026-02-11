package com.UserAuthMicroservice.models;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
public class Subscription extends BaseModel {

    @OneToOne(mappedBy = "subscription")
    private User user;

    @NonNull
    private SubscriptionType subscriptionType = SubscriptionType.FREE;
    
    @NonNull
    @Column(nullable=false)
    private Instant startDate;

    private Instant endDate = null;

}
