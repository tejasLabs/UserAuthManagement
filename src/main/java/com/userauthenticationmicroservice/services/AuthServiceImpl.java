package com.userauthenticationmicroservice.services;

import java.text.Normalizer;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.userauthenticationmicroservice.dtos.UserResponseDTO;
import com.userauthenticationmicroservice.exceptions.UserAlreadyExistsException;
import com.userauthenticationmicroservice.exceptions.UserNotFoundException;
import com.userauthenticationmicroservice.exceptions.UserSuspendedException;
import com.userauthenticationmicroservice.exceptions.WrongPasswordException;
import com.userauthenticationmicroservice.models.Role;
import com.userauthenticationmicroservice.models.Status;
import com.userauthenticationmicroservice.models.Subscription;
import com.userauthenticationmicroservice.models.SubscriptionType;
import com.userauthenticationmicroservice.models.User;
import com.userauthenticationmicroservice.repositories.RoleRepository;
import com.userauthenticationmicroservice.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_ROLE_VALUE = "NORMAL";

    @Override
    @Transactional
    public UserResponseDTO signup(String username, String email, String password) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        //Allow signup if no user exists with the email provided or if existing user if marked for deletion.
        if (existingUser.isPresent() && !existingUser.get().getStatus().equals(Status.DELETED)) {
                throw new UserAlreadyExistsException();
        }

        //Trim extra spaces in username and normalize it to NFC form to prevent issues with Unicode characters
        String usernameToUse = username.trim();
        usernameToUse = Normalizer.normalize(usernameToUse, Normalizer.Form.NFC);

        //Create a hash of the password using the default password Encoder before persisting it to the database
        String hashedPassword = passwordEncoder.encode(password);

        //Create a new user with provided details and populate the fields
        User newUser = new User();
        newUser.setUsername(usernameToUse);
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword);

        //Get the default role from the database to avoid duplicates in Role table and assign it to the new user. If the default role doesn't exist, create it and then assign it to the user.
        Role defaultRole = roleRepository.findByValue(DEFAULT_ROLE_VALUE)
                                            .orElseGet(() -> {
                                                Role role = new Role();
                                                role.setValue(DEFAULT_ROLE_VALUE);
                                                return roleRepository.save(role);
                                            });
        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);
        newUser.setRoles(roles);

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(SubscriptionType.FREE);
        subscription.setUser(newUser);
        subscription.setStartDate(Instant.now());
        subscription.setStatus(Status.INACTIVE); //All FREE tier subscriptions will be INACTIVE by default until user upgrades to a paid tier.
        newUser.setSubscription(subscription);

        // Persist the new user to the database and return a UserResponseDTO. Since CascadeType.ALL is set on the subscription field in User entity, the associated Subscription entity will also be persisted automatically when we save the User entity
        userRepository.save(newUser);

        return convertToDTO(newUser);
    }

    @Override
    public UserResponseDTO login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty() || userOptional.get().getStatus().equals(Status.DELETED)) {
            throw new UserNotFoundException();
        }
        if(userOptional.get().getStatus().equals(Status.SUSPENDED)){
            throw new UserSuspendedException();
        }
        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new WrongPasswordException();
        }

        return convertToDTO(user);
    }

    private UserResponseDTO convertToDTO(User user) {
        Set<String> rolesSet = new HashSet<>();
        for (Role role : user.getRoles()) {
            rolesSet.add(role.getValue());
        }
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(), rolesSet);
    }
}
