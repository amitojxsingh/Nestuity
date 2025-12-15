package com.nestuity.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nestuity.service.dto.BabyResponse;
import com.nestuity.service.dto.DiaperUsageResponse;
import com.nestuity.service.entity.Baby;
import com.nestuity.service.entity.User;
import com.nestuity.service.repository.BabyRepository;
import com.nestuity.service.repository.UserRepository;
import com.nestuity.service.service.BabyReminderService;
import com.nestuity.service.service.DiaperUsageCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BabyController.class)
class BabyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BabyRepository babyRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private DiaperUsageCalculatorService diaperUsageCalculatorService;

    @MockBean
    private BabyReminderService babyReminderService;

    private ObjectMapper objectMapper;

    private Baby testBaby;
    private User testUser;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("Parent");

        testBaby = new Baby();
        testBaby.setId(1L);
        testBaby.setName("Test Baby");
        testBaby.setWeight(3.0);
        testBaby.setDob(Date.valueOf("2025-01-01"));
        testBaby.setDiaperSize("1");
        testBaby.setDailyUsage(10);
        testBaby.setUser(testUser);
    }

    // ==================== CREATE ====================

    @Test
    void createBabyReturns200Test() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("userId", 1);
        body.put("name", "Test Baby");
        body.put("weight", 3.0);
        body.put("dob", "2025-01-01");
        body.put("diaperSize", "1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(babyRepository.save(any(Baby.class))).thenReturn(testBaby);

        mockMvc.perform(post("/api/babies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Baby"))
                .andExpect(jsonPath("$.dailyUsage").value(10));

        verify(babyRepository, times(1)).save(any(Baby.class));
    }

    @Test
    void createBabyWithInvalidUserReturns404Test() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("userId", 999);
        body.put("name", "Test Baby");
        body.put("weight", 3.0);
        body.put("dob", "2025-01-01");
        body.put("diaperSize", "1");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/babies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBabyWithInvalidWeightReturns400Test() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("userId", 1);
        body.put("name", "Test Baby");
        body.put("weight", -1.0);
        body.put("dob", "2025-01-01");
        body.put("diaperSize", "1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/babies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    // ==================== READ ====================

    @Test
    void getAllBabiesReturns200Test() throws Exception {
        when(babyRepository.findAll()).thenReturn(Collections.singletonList(testBaby));

        mockMvc.perform(get("/api/babies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Baby"));
    }

    @Test
    void getBabiesByUserIdReturns200Test() throws Exception {
        when(babyRepository.findAll()).thenReturn(Collections.singletonList(testBaby));

        mockMvc.perform(get("/api/babies/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Baby"))
                .andExpect(jsonPath("$[0].diaperSize").value("1"));
    }

    @Test
    void getBabyByIdReturns200Test() throws Exception {
        when(babyRepository.findById(1L)).thenReturn(Optional.of(testBaby));

        mockMvc.perform(get("/api/babies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Baby"));
    }

    @Test
    void getBabyByIdWithInvalidIdReturns404Test() throws Exception {
        when(babyRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/babies/999"))
                .andExpect(status().isNotFound());
    }

    // ==================== UPDATE ====================

    @Test
    void updateBabyReturns200Test() throws Exception {
        Baby updatedBaby = new Baby();
        updatedBaby.setName("Updated Baby");
        updatedBaby.setWeight(4.0);
        updatedBaby.setDob(Date.valueOf("2025-01-01"));
        updatedBaby.setDiaperSize("2");
        updatedBaby.setDailyUsage(-1);

        // Return fully populated Baby from save
        Baby savedBaby = new Baby();
        savedBaby.setId(1L);
        savedBaby.setName("Updated Baby");
        savedBaby.setWeight(4.0);
        savedBaby.setDob(Date.valueOf("2025-01-01"));
        savedBaby.setDiaperSize("2");
        savedBaby.setDailyUsage(10); // calculated
        savedBaby.setUser(testUser);

        when(babyRepository.findById(1L)).thenReturn(Optional.of(testBaby));
        when(babyRepository.save(any(Baby.class))).thenReturn(savedBaby);

        mockMvc.perform(put("/api/babies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBaby)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Baby"))
                .andExpect(jsonPath("$.dailyUsage").value(10));
    }

    @Test
    void updateBabyWithInvalidIdReturns404Test() throws Exception {
        Baby updatedBaby = new Baby();
        updatedBaby.setName("Updated Baby");
        updatedBaby.setWeight(3.0);
        updatedBaby.setDiaperSize("1");
        updatedBaby.setDob(Date.valueOf("2025-01-01"));
        updatedBaby.setDailyUsage(-1);

        when(babyRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/babies/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBaby)))
                .andExpect(status().isNotFound());
    }

    // ==================== DELETE ====================

    @Test
    void deleteBabyReturns200Test() throws Exception {
        when(babyRepository.findById(1L)).thenReturn(Optional.of(testBaby));
        doNothing().when(babyRepository).delete(testBaby);

        mockMvc.perform(delete("/api/babies/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Baby deleted successfully."));
    }

    @Test
    void deleteBabyWithInvalidIdReturns404Test() throws Exception {
        when(babyRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/babies/999"))
                .andExpect(status().isNotFound());
    }

    // ==================== DIAPER USAGE ====================

    @Test
    void getDiaperUsageReturns200Test() throws Exception {
        DiaperUsageResponse usageResponse = DiaperUsageResponse.builder()
                .remainingSupply(50.0)
                .daysLeft(5)
                .recommendedPurchase(10)
                .message("All good")
                .build();

        when(diaperUsageCalculatorService.calculateUsage(1L)).thenReturn(usageResponse);

        mockMvc.perform(get("/api/babies/1/diaper-usage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remainingSupply").value(50.0))
                .andExpect(jsonPath("$.daysLeft").value(5));
    }

    @Test
    void updateDiaperUsageReturns200Test() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("diapersUsed", 5);

        DiaperUsageResponse updatedUsage = DiaperUsageResponse.builder()
                .remainingSupply(45.0)
                .daysLeft(4)
                .recommendedPurchase(10)
                .message("Updated")
                .build();

        when(diaperUsageCalculatorService.updateUsage(1L, 5)).thenReturn(updatedUsage);

        mockMvc.perform(put("/api/babies/1/diaper-usage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remainingSupply").value(45.0))
                .andExpect(jsonPath("$.daysLeft").value(4));
    }
}
