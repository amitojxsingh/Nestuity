package com.nestuity.service.controller;

import com.nestuity.service.service.PriceScraperSchedulerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PriceScraperAdminController.class)
class PriceScraperAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceScraperSchedulerService scraperService;

    @BeforeEach
    void setUp() {
        // ensure we never trigger real external scraping
        Mockito.reset(scraperService);
    }

    // ---------------- STATUS ENDPOINT ----------------
    @Test
    void getStatus_ReturnsStatusInfo() throws Exception {
        Mockito.when(scraperService.getPythonPath()).thenReturn("/usr/bin/python3");
        Mockito.when(scraperService.getScriptPath()).thenReturn("/opt/scraper/scraper.py");

        mockMvc.perform(get("/api/admin/scraper/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.schedule").exists())
                .andExpect(jsonPath("$.pythonPath").value("/usr/bin/python3"))
                .andExpect(jsonPath("$.scriptPath").value("/opt/scraper/scraper.py"));
    }

    // ---------------- TOGGLE ENDPOINT ----------------
    @Test
    void toggleScraper_Enable_ReturnsSuccess() throws Exception {
        mockMvc.perform(post("/api/admin/scraper/toggle")
                        .param("enabled", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.message").value("Scraper enabled"))
                .andExpect(jsonPath("$.note").exists());
    }

    @Test
    void toggleScraper_Disable_ReturnsSuccess() throws Exception {
        mockMvc.perform(post("/api/admin/scraper/toggle")
                        .param("enabled", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.message").value("Scraper disabled"));
    }

    // ---------------- TRIGGER ENDPOINT (SAFE MOCKED) ----------------
    @Test
    void triggerScraper_Success_DoesNotActuallyRun() throws Exception {
        // Don’t actually trigger; just simulate success
        doNothing().when(scraperService).triggerManually();

        mockMvc.perform(post("/api/admin/scraper/trigger")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Price scraper triggered successfully"));
    }

    @Test
    void triggerScraper_Failure_ReturnsError() throws Exception {
        // Simulate exception to verify error handling
        doThrow(new RuntimeException("Simulated failure")).when(scraperService).triggerManually();

        mockMvc.perform(post("/api/admin/scraper/trigger"))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to trigger scraper: Simulated failure"));
    }
}
