package com.UserAuthMicroservice.services;

import java.text.Normalizer;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.UserAuthMicroservice.dtos.UserResponseDTO;
import com.UserAuthMicroservice.exceptions.UserNotFoundException;
import com.UserAuthMicroservice.exceptions.UserSuspendedException;
import com.UserAuthMicroservice.models.Role;
import com.UserAuthMicroservice.models.Status;
import com.UserAuthMicroservice.models.User;
import com.UserAuthMicroservice.repositories.UserRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    
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
        user.getRoles().add(new Role(newRole));
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

        //Ensure that user always has at least NORMAL role
        Role role = new Role("NORMAL");
        if(!user.getRoles().contains(role))
            user.getRoles().add(role);

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
            user.getRoles().stream().map(role -> role.getValue()).collect(java.util.stream.Collectors.toSet())
        );
    }
}
