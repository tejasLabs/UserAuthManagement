package com.UserAuthMicroservice.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.UserAuthMicroservice.dtos.SubscriptionResponseDTO;
import com.UserAuthMicroservice.exceptions.SubscriptionNotFoundException;
import com.UserAuthMicroservice.exceptions.UserNotFoundException;
import com.UserAuthMicroservice.exceptions.UserSuspendedException;
import com.UserAuthMicroservice.models.Status;
import com.UserAuthMicroservice.models.Subscription;
import com.UserAuthMicroservice.models.SubscriptionType;
import com.UserAuthMicroservice.models.User;
import com.UserAuthMicroservice.repositories.SubscriptionRepository;
import com.UserAuthMicroservice.repositories.UserRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

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

    @Transactional
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
        subscription.setEndDate(endDate);
        subscriptionRepository.save(subscription);
        return convertToDTO(subscription);
    }

    @Transactional
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
