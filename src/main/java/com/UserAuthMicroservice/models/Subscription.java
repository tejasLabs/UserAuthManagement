package com.UserAuthMicroservice.models;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
public class Subscription extends BaseModel {

    @OneToOne(mappedBy = "subscription")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private SubscriptionType subscriptionType = SubscriptionType.FREE;
    
    @Column(nullable=false)
    private Instant startDate;

    private Instant endDate = Instant.now();

}
