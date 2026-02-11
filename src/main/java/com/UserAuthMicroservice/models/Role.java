package com.UserAuthMicroservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String value;// e.g. "NORMAL", "ADMIN"

    public Role(String value) {
        this.value = value;
    }
}
