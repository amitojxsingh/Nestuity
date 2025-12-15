package com.nestuity.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nestuity.service.dto.BabyReminderDto;
import com.nestuity.service.service.BabyReminderService;
import com.nestuity.service.type.Frequency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit test for {@link BabyReminderController}.
 */
@WebMvcTest(BabyReminderController.class)
class BabyReminderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BabyReminderService reminderService;

    private ObjectMapper objectMapper;
    private BabyReminderDto testDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testDto = new BabyReminderDto();
        testDto.setId(1L);
        testDto.setTitle("Feed Baby");
        testDto.setDescription("Morning feeding task");
        testDto.setFrequency(Frequency.DAILY);
        testDto.setRequiresAction(true);
        testDto.setNotes("Use warm milk");
        testDto.setCompletedOn(LocalDateTime.now());
    }

    // ==================== CREATE TEST ====================

    @Test
    void createRemindersForBabyReturns200Test() throws Exception {
        doNothing().when(reminderService).createReminder(1L);

        mockMvc.perform(post("/api/reminders/1"))
                .andExpect(status().isOk());

        verify(reminderService, times(1)).createReminder(1L);
    }

    // ==================== READ TESTS ====================

    @Test
    void getReminderByIdReturns200Test() throws Exception {
        when(reminderService.getReminderById(1L)).thenReturn(Optional.of(testDto));

        mockMvc.perform(get("/api/reminders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Feed Baby"));

        verify(reminderService, times(1)).getReminderById(1L);
    }

    @Test
    void getReminderByIdReturns404WhenNotFoundTest() throws Exception {
        when(reminderService.getReminderById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reminders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRemindersByBabyIdReturns200Test() throws Exception {
        when(reminderService.getRemindersByBabyId(1L)).thenReturn(Arrays.asList(testDto));

        mockMvc.perform(get("/api/reminders/baby/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Feed Baby"));

        verify(reminderService, times(1)).getRemindersByBabyId(1L);
    }

    @Test
    void getRemindersByBabyIdReturnsEmptyArrayTest() throws Exception {
        when(reminderService.getRemindersByBabyId(2L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/reminders/baby/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    void getMedicalRemindersReturns200Test() throws Exception {
        // Mock service response
        when(reminderService.getMedicalReminders(1L))
                .thenReturn(List.of(testDto));

        // Perform GET request with query param ?type=medical
        mockMvc.perform(get("/api/reminders/baby/1/reminders")
                        .param("type", "medical"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Feed Baby"));
    }


    @Test
    void getOverdueRecurringReturns200Test() throws Exception {
        // Mock the service layer
        when(reminderService.getOverdueRecurring(1L))
                .thenReturn(List.of(testDto));

        // Perform GET request with query param ?type=overdue
        mockMvc.perform(get("/api/reminders/baby/1/reminders")
                        .param("type", "overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Feed Baby"));
    }


    // ==================== UPDATE TESTS ====================

    @Test
    void updateReminderReturns200Test() throws Exception {
        when(reminderService.updateReminder(eq(1L), any(BabyReminderDto.class))).thenReturn(testDto);

        mockMvc.perform(put("/api/reminders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Feed Baby"));

        verify(reminderService, times(1)).updateReminder(eq(1L), any(BabyReminderDto.class));
    }

    // ==================== COMPLETION TESTS ====================

    @Test
    void completeReminderReturns200Test() throws Exception {
        doNothing().when(reminderService).markAsCompleted(1L, false);

        mockMvc.perform(put("/api/reminders/1/complete"))
                .andExpect(status().isOk());

        verify(reminderService, times(1)).markAsCompleted(1L, false);
    }

    @Test
    void completeTaskReminderReturns200Test() throws Exception {
        doNothing().when(reminderService).markAsCompleted(1L, true);

        mockMvc.perform(delete("/api/reminders/1/task-complete"))
                .andExpect(status().isOk());

        verify(reminderService, times(1)).markAsCompleted(1L, true);
    }

    // ==================== DELETE TEST ====================

    @Test
    void deleteReminderReturns204Test() throws Exception {
        doNothing().when(reminderService).deleteReminder(1L);

        mockMvc.perform(delete("/api/reminders/1"))
                .andExpect(status().isNoContent());

        verify(reminderService, times(1)).deleteReminder(1L);
    }

    // ==================== CURRENT MILESTONE TEST ====================

    @Test
    void getCurrentMilestoneReturns200Test() throws Exception {
        when(reminderService.getCurrentMilestone(1L)).thenReturn(Optional.of(testDto));

        mockMvc.perform(get("/api/reminders/baby/1/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Feed Baby"));
    }
}
