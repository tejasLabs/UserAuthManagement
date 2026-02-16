package com.UserAuthMicroservice.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.UserAuthMicroservice.dtos.SubscriptionResponseDTO;
import com.UserAuthMicroservice.exceptions.SubscriptionNotFoundException;
import com.UserAuthMicroservice.models.Status;
import com.UserAuthMicroservice.models.Subscription;
import com.UserAuthMicroservice.models.SubscriptionType;
import com.UserAuthMicroservice.repositories.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionResponseDTO getSubscriptionDetails(UUID userId) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findByUserId(userId);
        if (subscriptionOpt.isEmpty()) {
            throw new SubscriptionNotFoundException();
        }
        return convertToDTO(subscriptionOpt.get());
    }

    public SubscriptionResponseDTO updateSubscription(UUID userId, String subscriptionType, Instant endDate) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findByUserId(userId);
        if (subscriptionOpt.isEmpty()) {
            throw new SubscriptionNotFoundException();
        }
        Subscription subscription = subscriptionOpt.get();
        subscription.setSubscriptionType(SubscriptionType.valueOf(subscriptionType));
        subscription.setStatus(Status.ACTIVE);
        subscription.setEndDate(endDate);
        subscriptionRepository.save(subscription);
        return convertToDTO(subscription);
    }

    public SubscriptionResponseDTO removeSubscription(UUID userId) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findByUserId(userId);
        if (subscriptionOpt.isEmpty()) {
            throw new SubscriptionNotFoundException();
        }
        Subscription subscription = subscriptionOpt.get();
        subscription.setSubscriptionType(SubscriptionType.FREE);
        subscription.setStatus(Status.INACTIVE);
        subscription.setEndDate(Instant.now());
        subscriptionRepository.save(subscription);
        return convertToDTO(subscription);
    }

    private SubscriptionResponseDTO convertToDTO(Subscription subscription) {
        return new SubscriptionResponseDTO(subscription.getSubscriptionType().toString(),
                subscription.getStatus().toString(), subscription.getStartDate(), subscription.getEndDate());
    }
}
