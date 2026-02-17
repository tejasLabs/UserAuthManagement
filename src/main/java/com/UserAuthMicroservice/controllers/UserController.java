package com.UserAuthMicroservice.controllers;


import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.UserAuthMicroservice.dtos.RoleRequestDTO;
import com.UserAuthMicroservice.dtos.UserNameRequestDTO;
import com.UserAuthMicroservice.dtos.UserResponseDTO;
import com.UserAuthMicroservice.services.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users/v1")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    
    @PatchMapping("/update/username")
    public ResponseEntity<UserResponseDTO> updateUserName(@Valid @RequestBody UserNameRequestDTO userNameRequestDTO){
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserName(userNameRequestDTO.userId(), userNameRequestDTO.username()));
    }

    @PatchMapping("/add/roles")
    public ResponseEntity<UserResponseDTO> addUserRole(@Valid @RequestBody RoleRequestDTO roleRequestDTO){
        return ResponseEntity.status(HttpStatus.OK).body(userService.addUserRole(roleRequestDTO.userId(), roleRequestDTO.role()));
    }

    @DeleteMapping("/remove/roles")
    public ResponseEntity<Void> removeUserRole(@Valid @RequestBody RoleRequestDTO roleRequestDTO){
        userService.removeUserRole(roleRequestDTO.userId(), roleRequestDTO.role());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("{userId}/delete")
    public ResponseEntity<String> softRemoveUser(@NotNull @PathVariable UUID userId){
        String userEmail = userService.softDeleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body("User account with email " + userEmail + " has been marked for deletion successfully.");
    }

    @DeleteMapping("{userId}/hardDelete")
    public ResponseEntity<Void> hardDeleteUser(@NotNull @PathVariable UUID userId){
        userService.hardDeleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
