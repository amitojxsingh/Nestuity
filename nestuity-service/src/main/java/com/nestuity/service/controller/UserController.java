package com.nestuity.service.controller;

import com.nestuity.service.dto.LoginRequest;
import com.nestuity.service.dto.PreferencesResponse;
import com.nestuity.service.dto.SaveUserRequest;
import com.nestuity.service.dto.UserResponse;
import com.nestuity.service.entity.User;
import com.nestuity.service.entity.UserPreferences;
import com.nestuity.service.service.UserService;
import com.nestuity.service.service.UserPreferencesService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserPreferencesService preferencesService;
    public UserController(UserService userService, UserPreferencesService preferencesService) {
        this.userService = userService;
        this.preferencesService = preferencesService;
    }

    // Users CRUD -----------------------------------------------------
    // Create a new user
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody SaveUserRequest request) {
        // 1. Prevent duplicates
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // 2. Create user entity
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAuthProvider(request.getAuthProvider());

        // 3. Handle provider differences
        if ("google".equalsIgnoreCase(request.getAuthProvider())) {
            // OAuth signup
            user.setProviderId(request.getProviderId());
            user.setPasswordHash(null); // No password for Google users
        } else {
            // Default to credentials-based signup
            user.setAuthProvider("credentials");
            if (request.getPassword() == null || request.getPassword().isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            user.setPasswordHash(request.getPassword());
        }

        // 4. Save the user
        User savedUser = userService.saveUser(user);

        // 5. Prepare response
        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setPhoneNumber(savedUser.getPhoneNumber()); // nullable
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setActive(savedUser.isActive());
        response.setCreatedAt(savedUser.getCreatedAt());
        response.setUpdatedAt(savedUser.getUpdatedAt());
        response.setPreferences(savedUser.getPreferences()); // nullable

        return getUserResponse(savedUser);
    }

    // Login with credentials
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> optionalUser = userService.getUserByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials: No matching email and password combination"));  // JSON object
        }

        User user = optionalUser.get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        boolean matches = encoder.matches(request.getPassword(), user.getPasswordHash());
        if (!matches) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials : No matching email and password combination"));  // JSON object
        }
        return getUserResponse(user);
    }

    // Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    // Get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // Update a user
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody SaveUserRequest request) {

        Optional<User> existingUserOpt = userService.getUserById(id);
        if (existingUserOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User existingUser = getExistingUser(request, existingUserOpt);

        User savedUser = userService.saveUser(existingUser);

        // Build a response DTO
        return getUserResponse(savedUser);
    }

    // Delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<User> existingUser = userService.getUserById(id);
        if (existingUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // Delete preferences first
        preferencesService.deletePreferencesByUserId(id);
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
    // TOOD: should I handle deactivation/reactivation of a user? --> isActive field


    // Preferences -----------------------------------------------------
    // Get preferences for a user
    @GetMapping("/{id}/preferences")
    public ResponseEntity<UserPreferences> getPreferences(@PathVariable Long id) {
        Optional<UserPreferences> prefs = preferencesService.getPreferencesByUserId(id);
        return prefs.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // Update or create preferences for a user
    @PutMapping("/{id}/preferences")
    public ResponseEntity<UserPreferences> updatePreferences(
            @PathVariable Long id,
            @RequestBody UserPreferences prefsRequest
    ) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        UserPreferences updatedPreferences = userService.updatePreferences(id, prefsRequest);
        return ResponseEntity.ok(updatedPreferences);
    }
    // Delete preferences for a user
    @DeleteMapping("/{id}/preferences")
    public ResponseEntity<Void> deletePreferences(@PathVariable Long id) {
        preferencesService.deletePreferencesByUserId(id);
        return ResponseEntity.noContent().build();
    }


    // HELPER METHODS ------------------------------------------------------
    private static User getExistingUser(SaveUserRequest request, Optional<User> existingUserOpt) {
        User existingUser = existingUserOpt.orElse(new User());

        // Map request fields into entity (partial update)
        if (request.getEmail() != null) existingUser.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) existingUser.setPhoneNumber(request.getPhoneNumber());
        if (request.getPassword() != null) existingUser.setPasswordHash(request.getPassword());
        if (request.getFirstName() != null) existingUser.setFirstName(request.getFirstName());
        if (request.getLastName() != null) existingUser.setLastName(request.getLastName());
        if (request.getAuthProvider() != null) existingUser.setAuthProvider(request.getAuthProvider());
        if (request.getProviderId() != null) existingUser.setProviderId(request.getProviderId());
        return existingUser;
    }

    private ResponseEntity<UserResponse> getUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhoneNumber(user.getPhoneNumber()); // nullable
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setActive(user.isActive());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
        userResponse.setPreferences(user.getPreferences()); // nullable
        return ResponseEntity.ok(userResponse);
    }

}
