package com.UserAuthMicroservice.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.UserAuthMicroservice.models.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    
}
