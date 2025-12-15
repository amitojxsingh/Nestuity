package com.nestuity.service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String currency; // e.g., "USD", "EUR"
    private String timezone;
    private boolean emailNotificationsEnabled;
    private boolean smsNotificationsEnabled;

    // TODO: specific types of notifications?
    // - price drop alerts
    // - low stock alerts
    // - summaries
    // - etc.
}