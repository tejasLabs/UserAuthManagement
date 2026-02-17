package com.UserAuthMicroservice.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.UserAuthMicroservice.dtos.SubscriptionRequestDTO;
import com.UserAuthMicroservice.dtos.SubscriptionResponseDTO;
import com.UserAuthMicroservice.services.SubscriptionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("subscriptions/v1")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    //Get user's subscription details
    @GetMapping("/{userId}")
    public ResponseEntity<SubscriptionResponseDTO> getSubscriptionUsingUserId(@NotNull @PathVariable UUID userId){
        return ResponseEntity.status(HttpStatus.OK).body(subscriptionService.getSubscriptionDetails(userId));
    }

    //Update user's subscription details (subscription type and end date)
    @PatchMapping("/update")
    public ResponseEntity<SubscriptionResponseDTO> updateSubscriptionUsingUserId(
            @Valid @RequestBody SubscriptionRequestDTO request){
        return ResponseEntity.status(HttpStatus.OK).body(subscriptionService.updateSubscription(request.userId(),request.subscriptionType(), request.endDate()));
    }

    //Remove user's subscription (set subscriptionType to FREE, status to INACTIVE and endDate to current date)
    @PatchMapping("/remove/{userId}")
    public ResponseEntity<SubscriptionResponseDTO> removeSubscriptionUsingUserId(@NotNull @PathVariable UUID userId){
        return ResponseEntity.status(HttpStatus.OK).body(subscriptionService.removeSubscription(userId));
    }

}
