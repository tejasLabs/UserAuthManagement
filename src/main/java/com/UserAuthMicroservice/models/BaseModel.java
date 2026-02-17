package com.UserAuthMicroservice.models;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;


@MappedSuperclass
@Data
public abstract class BaseModel {
    //Use UUID v7 for better performance and scalability compare to UUID v4 as it is time-ordered and optimized. This will improve database indexing and query performance, especially as the number of records grows.
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(nullable=false, updatable = false)
    private Instant createdAt;

    @Column(nullable=false)
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length = 16)
    private Status status = Status.ACTIVE;

    @PrePersist
    protected void onCreate(){
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = Instant.now();
    }
}
