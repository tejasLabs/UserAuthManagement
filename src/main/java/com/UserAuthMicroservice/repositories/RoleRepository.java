package com.UserAuthMicroservice.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.UserAuthMicroservice.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByValue(String value);
}
