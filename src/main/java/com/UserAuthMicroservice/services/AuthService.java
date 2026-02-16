package com.UserAuthMicroservice.services;

import java.text.Normalizer;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.UserAuthMicroservice.dtos.RoleDTO;
import com.UserAuthMicroservice.dtos.UserResponseDTO;
import com.UserAuthMicroservice.exceptions.UserAlreadyExistsException;
import com.UserAuthMicroservice.exceptions.UserNotFoundException;
import com.UserAuthMicroservice.exceptions.WrongPasswordException;
import com.UserAuthMicroservice.models.Role;
import com.UserAuthMicroservice.models.Status;
import com.UserAuthMicroservice.models.Subscription;
import com.UserAuthMicroservice.models.SubscriptionType;
import com.UserAuthMicroservice.models.User;
import com.UserAuthMicroservice.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_ROLE_VALUE = "NORMAL";

    @Override
    public UserResponseDTO signup(String username, String email, String password) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException();
        }

        // Trim extra spaces in username and normalize it to NFC form to prevent issues
        // with Unicode characters
        String usernameToUse = username.trim();
        usernameToUse = Normalizer.normalize(usernameToUse, Normalizer.Form.NFC);

        // Create a hash of the password using the default password Encoder before
        // persisting it to the database
        String hashedPassword = passwordEncoder.encode(password);

        // Create a new user with provided details and populate the fields
        User newUser = new User();
        newUser.setUsername(usernameToUse);
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword);

        Role defaultRole = new Role();
        defaultRole.setValue(DEFAULT_ROLE_VALUE);
        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);
        newUser.setRoles(roles);

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(SubscriptionType.FREE);
        subscription.setUser(newUser);
        subscription.setStartDate(Instant.now());
        subscription.setStatus(Status.INACTIVE); //All FREE tier subscriptions will be INACTIVE by default until user upgrades to a paid tier.
        newUser.setSubscription(subscription);

        // Persist the new user to the database and return a UserResponseDTO
        // Since CascadeType.ALL is set on the subscription field in User entity, the
        // associated Subscription entity will also be persisted automatically when we
        // save the User entity
        userRepository.save(newUser);

        return convertToDTO(newUser);
    }

    @Override
    public UserResponseDTO login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException();
        }
        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new WrongPasswordException();
        }

        return convertToDTO(user);
    }

    private UserResponseDTO convertToDTO(User user) {
        Set<RoleDTO> roleDTOSet = new HashSet<>();
        for (Role role : user.getRoles()) {
            roleDTOSet.add(new RoleDTO(role.getValue()));
        }
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(), roleDTOSet);
    }
}
