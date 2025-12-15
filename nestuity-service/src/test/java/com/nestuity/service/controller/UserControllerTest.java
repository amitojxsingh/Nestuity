package com.nestuity.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nestuity.service.dto.SaveUserRequest;
import com.nestuity.service.entity.User;
import com.nestuity.service.service.UserPreferencesService;
import com.nestuity.service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserPreferencesService preferencesService;

    private ObjectMapper objectMapper;
    private User sampleUser;
    private SaveUserRequest sampleSaveUserRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // <-- important for ZonedDateTime

        // TODO: Pull out to general file with other sample objects
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

        sampleSaveUserRequest = new SaveUserRequest();
        sampleSaveUserRequest.setFirstName("John");
        sampleSaveUserRequest.setLastName("Doe");
        sampleSaveUserRequest.setEmail("test@example.com");
        sampleSaveUserRequest.setPhoneNumber("1234567890");
        sampleSaveUserRequest.setPassword("hashedpassword");
    }

    // ==================== CREATE ====================

    @Test
    void createUser_Success() throws Exception {
        when(userService.existsByEmail("test@example.com")).thenReturn(false);
        when(userService.saveUser(any(User.class))).thenReturn(sampleUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleSaveUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void createUser_Conflict() throws Exception {
        when(userService.existsByEmail("test@example.com")).thenReturn(true);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isConflict());
    }

    // ==================== READ ====================

    @Test
    void getAllUsers_Success() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(sampleUser));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    void getUserById_Found() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void getUserById_NotFound() throws Exception {
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    // ==================== UPDATE ====================

    @Test
    void updateUser_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(sampleUser));
        when(userService.saveUser(any(User.class))).thenReturn(sampleUser);

        sampleUser.setFirstName("UpdatedName");

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("UpdatedName"));
    }

    @Test
    void updateUser_NotFound() throws Exception {
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isNotFound());
    }

    // ==================== DELETE ====================

    @Test
    void deleteUser_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(sampleUser));
        Mockito.doNothing().when(preferencesService).deletePreferencesByUserId(1L);
        Mockito.doNothing().when(userService).deleteUserById(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_NotFound() throws Exception {
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());
    }
}
