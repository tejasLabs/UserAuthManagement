package com.UserAuthMicroservice.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
public class User extends BaseModel {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    // Setting CascadeType.ALL to ensure that all operations (persist, merge,
    // remove, refresh, detach) are cascaded to the associated Role entity
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false)
    private String password;

    // Set subscription_id as the foreign key to Subscription table
    // Setting CascadeType.ALL to ensure that all operations (persist, merge,
    // remove, refresh, detach) are cascaded to the associated Subscription entity
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "subscription_id", referencedColumnName = "id", nullable = false)
    private Subscription subscription;

}