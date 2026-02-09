package com.UserAuthMicroservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "users")
public class User extends BaseModel {
    
    @NonNull
    @Column(nullable=false, unique=true)
    private String username;

    @NonNull
    @Column(nullable=false, unique=true)
    private String email;

    @Enumerated(EnumType.STRING)
    @NonNull
    @Column(nullable=false, length = 16)
    private UserType userType;

    @NonNull
    @ManyToOne(optional=false)
    private Subscription subscription;

}