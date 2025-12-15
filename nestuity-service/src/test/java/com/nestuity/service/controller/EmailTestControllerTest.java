package com.nestuity.service.controller;

import com.nestuity.service.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmailTestController.class)
class EmailTestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doNothing().when(emailService).sendWelcomeEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("Returns success message when emailService works")
    void sendWelcomeEmail_returnsSuccess() throws Exception {
        mockMvc.perform(get("/email/welcome")
                        .param("to", "fake@example.com")
                        .param("username", "TestUser"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Welcome email sent successfully")));
    }

    @Test
    @DisplayName("Returns error message if emailService throws exception")
    void sendWelcomeEmail_handlesFailure() throws Exception {
        Mockito.doThrow(new RuntimeException("Simulated failure"))
                .when(emailService).sendWelcomeEmail(anyString(), anyString());

        mockMvc.perform(get("/email/welcome")
                        .param("to", "fake@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error sending email")));
    }
}
