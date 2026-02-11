package com.UserAuthMicroservice.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
public class User extends BaseModel {

    @NonNull
    @Column(nullable = false)
    private String username;

    @NonNull
    @Column(nullable = false, unique = true)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    @NonNull
    @OneToOne(optional = false)
    private Subscription subscription;

}