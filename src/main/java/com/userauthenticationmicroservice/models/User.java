package com.userauthenticationmicroservice.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseModel {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false)
    private String password;

    // Set subscription_id as the foreign key to Subscription table. Setting CascadeType.ALL to ensure that all operations (persist, merge, remove, refresh, detach) are cascaded to the associated Subscription entity.
    // Setting orphanRemoval=true to automatically delete the associated Subscription when a User is deleted, ensuring data integrity and preventing orphaned records in the Subscription table.
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true,optional = false)
    @JoinColumn(name = "subscription_id", referencedColumnName = "id", nullable = false)
    private Subscription subscription;

}