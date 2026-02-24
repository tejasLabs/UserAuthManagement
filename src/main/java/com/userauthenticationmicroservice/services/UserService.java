package com.userauthenticationmicroservice.services;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.userauthenticationmicroservice.dtos.RoleResponseDTO;
import com.userauthenticationmicroservice.dtos.UserResponseDTO;
import com.userauthenticationmicroservice.exceptions.UserNotFoundException;
import com.userauthenticationmicroservice.exceptions.UserSuspendedException;
import com.userauthenticationmicroservice.models.Role;
import com.userauthenticationmicroservice.models.Status;
import com.userauthenticationmicroservice.models.User;
import com.userauthenticationmicroservice.repositories.RoleRepository;
import com.userauthenticationmicroservice.repositories.UserRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    public RoleResponseDTO getUserRoles(@NonNull UUID userId){
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getStatus().equals(Status.DELETED)) {
            throw new UserNotFoundException();
        }
        else if(userOpt.get().getStatus().equals(Status.SUSPENDED)){
            throw new UserSuspendedException();
        }
        User user = userOpt.get();
        Set<String> roleValues = new HashSet<>();
        for(Role role:user.getRoles()){
            roleValues.add(role.getValue());
        }
        return new RoleResponseDTO(userId, roleValues);
    }
    
    public UserResponseDTO updateUserName(@NonNull UUID userId, String newUserName) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getStatus().equals(Status.DELETED)) {
            throw new UserNotFoundException();
        }
        else if(userOpt.get().getStatus().equals(Status.SUSPENDED)){
            throw new UserSuspendedException();
        }

        // Trim extra spaces in username and normalize it to NFC form to prevent issues
        // with Unicode characters
        String usernameToUse = newUserName.trim();
        usernameToUse = Normalizer.normalize(usernameToUse, Normalizer.Form.NFC);

        User user = userOpt.get();
        user.setUsername(usernameToUse);
        userRepository.save(user);
        return converToDTO(user);
    }

    public UserResponseDTO addUserRole(@NonNull UUID userId, String newRole) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getStatus().equals(Status.DELETED)) {
            throw new UserNotFoundException();
        }
        else if(userOpt.get().getStatus().equals(Status.SUSPENDED)){
            throw new UserSuspendedException();
        }

        User user = userOpt.get();
        Role roleToAdd = roleRepository.findByValue(newRole)
                                            .orElseGet(() -> roleRepository.save(new Role(newRole)));
        user.getRoles().add(roleToAdd);
        userRepository.save(user);
        return converToDTO(user);
    }

    public Void removeUserRole(@NonNull UUID userId, String roleToRemove){
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getStatus().equals(Status.DELETED)) {
            throw new UserNotFoundException();
        }
        else if(userOpt.get().getStatus().equals(Status.SUSPENDED)){
            throw new UserSuspendedException();
        }

        User user = userOpt.get();
        user.getRoles().removeIf(role -> Objects.equals(role.getValue(), roleToRemove));

        userRepository.save(user);
        return null;
    }

    public String softDeleteUser(@NonNull UUID userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getStatus().equals(Status.DELETED)) {
            throw new UserNotFoundException();
        }

        User user = userOpt.get();
        user.setStatus(Status.DELETED); //Mark user for deletion
        user.getSubscription().setStatus(Status.DELETED); //Mark user's subscription as DELETED immediately to prevent any further access to premium features if they have a paid subscription.
        userRepository.save(user);
        return user.getEmail();
    }

    public Void hardDeleteUser(@NonNull UUID userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException();
        }

        userRepository.deleteById(userId); //Delete user entirely from the database, this is irreversible
        return null;
    }

    private UserResponseDTO converToDTO(User user){
        return new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRoles().stream().map(role -> role.getValue()).collect(java.util.stream.Collectors.toSet()),
            null
        );
    }
}
