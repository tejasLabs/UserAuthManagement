package com.userauthenticationmicroservice.models;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.Hibernate;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;


@MappedSuperclass
@Getter
@Setter
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
    @Column(nullable=false, length = 20)
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

    @Override
    public final boolean equals(Object o){
        if(this == o) return true;
        if(o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BaseModel that = (BaseModel)o;
        return this.id != null && this.id.equals(that.id);
    }

    @Override public final int hashCode(){
        return (id!=null)? id.hashCode() : Hibernate.getClass(this).hashCode();
    }
}
