package com.userauthenticationmicroservice.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.userauthenticationmicroservice.dtos.RoleRequestDTO;
import com.userauthenticationmicroservice.dtos.SortParam;
import com.userauthenticationmicroservice.dtos.SubscriptionRequestDTO;
import com.userauthenticationmicroservice.models.SubscriptionType;
import com.userauthenticationmicroservice.repositories.projections.UserInfo;
import com.userauthenticationmicroservice.repositories.projections.UserSummary;
import com.userauthenticationmicroservice.services.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {
    
    private final AdminService adminService;

    @GetMapping("/users")
    public Page<UserSummary> getAllUsers(@RequestParam(required = true,defaultValue = "0") int page, @RequestParam(required = false,defaultValue = "10") int size, @RequestParam(required = false) List<SortParam> sortParams){

        return adminService.getAllUsers(page, size, sortParams);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserInfo> getUserInfo(@PathVariable UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getUserInfo(userId));
    }

    @PatchMapping("/users/{userId}/suspend")
    public ResponseEntity<String> suspendUser(@NonNull @PathVariable UUID userId) {
        adminService.suspendUser(userId);
        return ResponseEntity.ok("User " + userId + " suspended successfully.");
    }

    @PatchMapping("/users/{userId}/restore")
    public ResponseEntity<String> activateUser(@NonNull @PathVariable UUID userId) {
        adminService.restoreUser(userId);
        return ResponseEntity.ok("User " + userId + " activated successfully.");
    }

    @PatchMapping("/users/{userId}/delete")
    public ResponseEntity<String> deleteUser(@NonNull @PathVariable UUID userId) {
        adminService.softDeleteUser(userId);
        return ResponseEntity.ok("User " + userId + " marked for deletion successfully.");
    }

    @PatchMapping("/users/{userId}/hardDelete")
    public ResponseEntity<String> hardDeleteUser(@NonNull @PathVariable UUID userId){
        adminService.hardDeleteUser(userId);
        return ResponseEntity.ok("User " + userId + " deleted permanently.");
    }

    @PatchMapping("/users/updateSubscription")
    public ResponseEntity<String> updateSubscription(@Valid @RequestBody SubscriptionRequestDTO subscriptionRequestDto){
        adminService.updateSubscription(subscriptionRequestDto.userId(), subscriptionRequestDto.subscriptionType(), subscriptionRequestDto.endDate());
        return ResponseEntity.ok("User " + subscriptionRequestDto.userId() + " subscription updated successfully with" + (subscriptionRequestDto.subscriptionType() != null ? " subscription type: " + subscriptionRequestDto.subscriptionType() : "") + (!subscriptionRequestDto.subscriptionType().equals(SubscriptionType.LIFETIME.toString()) ? " end date: " + subscriptionRequestDto.endDate() : " end date: NA"));
    }

    @PostMapping("/users/add/roles")
    public ResponseEntity<String> addUserRole(@Valid @RequestBody RoleRequestDTO roleRequestDTO){
        adminService.addUserRole(roleRequestDTO.userId(), roleRequestDTO.role());
        return ResponseEntity.status(HttpStatus.CREATED).body("User " + roleRequestDTO.userId() + " has been assigned the role " + roleRequestDTO.role() + " successfully.");
    }

    @DeleteMapping("/users/remove/roles")
    public ResponseEntity<String> removeUserRole(@Valid @RequestBody RoleRequestDTO roleRequestDTO){
        adminService.removeUserRole(roleRequestDTO.userId(), roleRequestDTO.role());
        return ResponseEntity.status(HttpStatus.OK).body("Role " + roleRequestDTO.role() + " has been removed from user " + roleRequestDTO.userId() + " successfully.");
    }
}
