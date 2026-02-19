package com.userauthenticationmicroservice.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.userauthenticationmicroservice.dtos.SubscriptionResponseDTO;
import com.userauthenticationmicroservice.exceptions.SubscriptionNotFoundException;
import com.userauthenticationmicroservice.exceptions.UserNotFoundException;
import com.userauthenticationmicroservice.exceptions.UserSuspendedException;
import com.userauthenticationmicroservice.models.Status;
import com.userauthenticationmicroservice.models.Subscription;
import com.userauthenticationmicroservice.models.SubscriptionType;
import com.userauthenticationmicroservice.models.User;
import com.userauthenticationmicroservice.repositories.SubscriptionRepository;
import com.userauthenticationmicroservice.repositories.UserRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public SubscriptionResponseDTO getSubscriptionDetails(@NonNull UUID userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getStatus().equals(Status.DELETED)) {
            throw new UserNotFoundException();
        }
        else if(userOpt.get().getStatus().equals(Status.SUSPENDED)){
            throw new UserSuspendedException();
        }
        User user = userOpt.get();
        Subscription subscription = user.getSubscription();
        if (subscription == null) {
            throw new SubscriptionNotFoundException();
        }
        return convertToDTO(subscription);
    }

    public SubscriptionResponseDTO updateSubscription(@NonNull UUID userId, String subscriptionType, Instant endDate) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getStatus().equals(Status.DELETED)) {
            throw new UserNotFoundException();
        }
        else if(userOpt.get().getStatus().equals(Status.SUSPENDED)){
            throw new UserSuspendedException();
        }
        User user = userOpt.get();
        Subscription subscription = user.getSubscription();
        if (subscription == null) {
            throw new SubscriptionNotFoundException();
        }

        subscription.setSubscriptionType(SubscriptionType.valueOf(subscriptionType));
        subscription.setStatus(Status.ACTIVE);
        if(subscriptionType.equals(SubscriptionType.LIFETIME.toString())){
            subscription.setEndDate(null);
        }
        else{
            subscription.setEndDate(endDate);
        }
        subscriptionRepository.save(subscription);
        return convertToDTO(subscription);
    }

    public SubscriptionResponseDTO removeSubscription(@NonNull UUID userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getStatus().equals(Status.DELETED)) {
            throw new UserNotFoundException();
        }
        else if(userOpt.get().getStatus().equals(Status.SUSPENDED)){
            throw new UserSuspendedException();
        }
        User user = userOpt.get();
        Subscription subscription = user.getSubscription();
        if (subscription == null) {
            throw new SubscriptionNotFoundException();
        }

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
