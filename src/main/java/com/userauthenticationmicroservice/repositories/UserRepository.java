package com.userauthenticationmicroservice.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.userauthenticationmicroservice.models.User;
import com.userauthenticationmicroservice.repositories.projections.UserInfo;

public interface UserRepository extends JpaRepository<User, UUID> {
    //Entity Graph is a JPA feature (not Hibernate-specific) that is used to define a fetch plan dynamically. It changes the query to add - 'When fetching this entity, also fetch these relationships eagerly in the same query.'
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    //Custom method to fetch paginated UserSummary projections
    <T> Page<T> findAllProjectedBy(Pageable pageable, Class<T> type);

    @EntityGraph(attributePaths = {"roles","subscription"})
    UserInfo findAllProjectedById(UUID userId);

}
