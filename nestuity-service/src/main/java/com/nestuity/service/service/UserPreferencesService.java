package com.nestuity.service.service;

import com.nestuity.service.entity.UserPreferences;
import com.nestuity.service.repository.UserPreferencesRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserPreferencesService {
    private final UserPreferencesRepository userPreferencesRepository;

    public UserPreferencesService(UserPreferencesRepository userPreferencesRepository) {
        this.userPreferencesRepository = userPreferencesRepository;
    }

    // Create or update preferences for a user
    public UserPreferences savePreferences(UserPreferences preferences) {
        return userPreferencesRepository.save(preferences);
    }

    // Get preferences by user ID
    public Optional<UserPreferences> getPreferencesByUserId(Long userId) {
        return userPreferencesRepository.findById(userId);
    }

    // Delete preferences for a user
    public void deletePreferencesByUserId(Long id) {
        userPreferencesRepository.deleteById(id);
    }
}