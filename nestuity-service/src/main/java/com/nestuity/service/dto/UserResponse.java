package com.nestuity.service.dto;

import com.nestuity.service.entity.UserPreferences;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Nullable
    private String phoneNumber;

    @Nullable
    private UserPreferences preferences;
}