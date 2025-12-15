package com.nestuity.service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.nestuity.service.entity.UserPreferences;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nestuity_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    private String phoneNumber;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwordHash;

    // "credentials" or "google" for now
    @Column(nullable = false)
    private String authProvider = "credentials";

    // for google auth
    private String providerId;

    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private boolean isActive;


    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "preferences_id", referencedColumnName = "id")
    private UserPreferences preferences;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    // One User can have many babies
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Baby> babies = new ArrayList<>();

    // Each user can have multiple Inventory items
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Inventory> inventory = new ArrayList<>();

    // NOTE: getters/setters not necessary as lombok annotation makes that already
}