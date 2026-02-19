package com.userauthenticationmicroservice.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.userauthenticationmicroservice.models.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    //Entity Graph is a JPA feature (not Hibernate-specific) that is used to define a fetch plan dynamically. It changes the query to add - 'When fetching this entity, also fetch these relationships eagerly in the same query.'
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

}
