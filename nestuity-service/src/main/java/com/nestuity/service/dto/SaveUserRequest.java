package com.nestuity.service.dto;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class SaveUserRequest {
    private String email;

    @Nullable
    private String phoneNumber;

    @Nullable
    private String password;

    @Nullable
    private String firstName;

    @Nullable
    private String lastName;

    @Nullable
    private String authProvider;

    @Nullable
    private String providerId;
}
