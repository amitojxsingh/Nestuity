package com.nestuity.service.service;

import com.nestuity.service.entity.User;
import com.nestuity.service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);

        sampleUser = new User(
            new Random().nextLong(),
            "test@example.com",
            "1234567890",
            "hashedpassword",
            "credentials",    // authProvider
            null,             // providerId
            "John",
            "Doe",
            true,
            null,             // preferences
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            List.of(),
            List.of()
        );
    }

    // ==================== CREATE / UPDATE ====================

    @Test
    void saveUser_Success() {
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        User saved = userService.saveUser(sampleUser);

        assertNotNull(saved);
        assertEquals("test@example.com", saved.getEmail());
        verify(userRepository, times(1)).save(sampleUser);
    }

    // ==================== READ ====================

    @Test
    void getUserById_Found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void getUserByEmail_Found() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(sampleUser));

        Optional<User> result = userService.getUserByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
    }

    @Test
    void getUserByEmail_NotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByEmail("unknown@example.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void existsByEmail_True() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertTrue(userService.existsByEmail("test@example.com"));
    }

    @Test
    void existsByEmail_False() {
        when(userRepository.existsByEmail("unknown@example.com")).thenReturn(false);

        assertFalse(userService.existsByEmail("unknown@example.com"));
    }

    @Test
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(sampleUser));

        List<User> users = userService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals("test@example.com", users.get(0).getEmail());
    }

    // ==================== DELETE ====================

    @Test
    void deleteUserById_Success() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUserById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_Success() {
        doNothing().when(userRepository).delete(sampleUser);

        userService.deleteUser(sampleUser);

        verify(userRepository, times(1)).delete(sampleUser);
    }
}
