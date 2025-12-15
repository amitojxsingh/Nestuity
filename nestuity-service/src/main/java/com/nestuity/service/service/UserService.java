package com.nestuity.service.service;

import com.nestuity.service.entity.User;
import com.nestuity.service.entity.UserPreferences;
import com.nestuity.service.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Save a new user or update an existing one
    public User saveUser(User user) {
        System.out.println("=== saveUser START ===");

        Optional<User> existingUserOpt = (user.getId() != null)
                ? userRepository.findById(user.getId())
                : userRepository.findByEmail(user.getEmail());

        User existingUser = existingUserOpt.orElse(null);

        if (existingUser != null) {
            // Preserve immutable or sensitive fields
            user.setId(existingUser.getId());
            user.setAuthProvider(existingUser.getAuthProvider());
            user.setProviderId(existingUser.getProviderId());
            user.setCreatedAt(existingUser.getCreatedAt());

            // Keep existing preferences if none provided
            if (user.getPreferences() == null) {
                user.setPreferences(existingUser.getPreferences());
            }

            // If password is null or blank, keep the old one
            if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
                user.setPasswordHash(existingUser.getPasswordHash());
            }
        }

        // Handle password logic depending on auth provider
        if ("credentials".equalsIgnoreCase(user.getAuthProvider())) {
            if (user.getPasswordHash() != null && !user.getPasswordHash().startsWith("$2a$")) {
                System.out.println("Encoding password");
                user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            }
        } else {
            // OAuth users don’t have a password
            user.setPasswordHash(null);
        }

        // Create default preferences if none exist at all
        if (user.getPreferences() == null) {
            UserPreferences defaultPrefs = new UserPreferences();
            defaultPrefs.setCurrency("CAD");
            defaultPrefs.setTimezone("America/Edmonton");
            defaultPrefs.setEmailNotificationsEnabled(true);
            defaultPrefs.setSmsNotificationsEnabled(true);
            user.setPreferences(defaultPrefs);
        }

        user.setActive(true);
        user.setUpdatedAt(ZonedDateTime.now());
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(ZonedDateTime.now());
        }

        User saved = userRepository.save(user);
        System.out.println("=== saveUser END ===");

        return saved;
    }

    // Find user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Find user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Check if a user exists by email
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Delete user by ID
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    // Delete a user entity
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public UserPreferences updatePreferences(Long userId, UserPreferences prefsRequest) {
        User user = this.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserPreferences preferences = user.getPreferences();
        if (preferences == null) {
            preferences = new UserPreferences();
        }
        if (prefsRequest.getCurrency() != null) {
            preferences.setCurrency(prefsRequest.getCurrency());
        }
        if (prefsRequest.getTimezone() != null) {
            preferences.setTimezone(prefsRequest.getTimezone());
        }
        preferences.setEmailNotificationsEnabled(prefsRequest.isEmailNotificationsEnabled());
        preferences.setSmsNotificationsEnabled(prefsRequest.isSmsNotificationsEnabled());
        user.setPreferences(preferences);
        this.saveUser(user);
        return preferences;
    }

}
