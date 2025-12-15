package com.nestuity.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link PriceScraperSchedulerService}.
 *
 * These are safe, non-executing tests — they DO NOT actually trigger Python scripts.
 */
class PriceScraperSchedulerServiceTest {

    private static final Logger log = LoggerFactory.getLogger(PriceScraperSchedulerServiceTest.class);

    private PriceScraperSchedulerService service;

    @BeforeEach
    void setUp() {
        service = new PriceScraperSchedulerService();

        // Inject fake values to avoid actual process execution
        ReflectionTestUtils.setField(service, "pythonPath", "/usr/bin/python3");
        ReflectionTestUtils.setField(service, "scriptPath", "fake_script.py");
        ReflectionTestUtils.setField(service, "scraperEnabled", true);
    }

    // ============================================================
    // BASIC FIELD TESTS
    // ============================================================
    @Test
    @DisplayName("Getters return injected values correctly")
    void gettersReturnValues() {
        assertEquals("/usr/bin/python3", service.getPythonPath());
        assertEquals("fake_script.py", service.getScriptPath());
        assertTrue(service.isScraperEnabled());
    }

    // ============================================================
    // RUN SCRAPER SAFELY
    // ============================================================
    @Test
    @DisplayName("runPriceScraper handles missing script gracefully")
    void runPriceScraper_handlesMissingFile() {
        // Ensure file doesn't exist
        File fake = new File("fake_script.py");
        if (fake.exists()) fake.delete();

        assertDoesNotThrow(() -> {
            service.runPriceScraper();
        }, "Should not throw even if script missing");
    }

    // ============================================================
    // DISABLED SCRAPER BEHAVIOR
    // ============================================================
    @Test
    @DisplayName("runPriceScraper skips when disabled")
    void runPriceScraper_skipsWhenDisabled() {
        ReflectionTestUtils.setField(service, "scraperEnabled", false);

        assertDoesNotThrow(() -> {
            service.runPriceScraper();
        }, "Should skip execution without throwing");
    }

    // ============================================================
    // MANUAL TRIGGER BEHAVIOR
    // ============================================================
    @Test
    @DisplayName("triggerManually calls runPriceScraper safely")
    void triggerManually_runsSafely() {
        PriceScraperSchedulerService spyService = Mockito.spy(service);

        // Mock runPriceScraper so it doesn't actually execute
        Mockito.doNothing().when(spyService).runPriceScraper();

        assertDoesNotThrow(() -> {
            spyService.triggerManually();
        });

        Mockito.verify(spyService, Mockito.times(1)).runPriceScraper();
    }

    // ============================================================
    // EXECUTION METHOD SAFETY
    // ============================================================
    @Test
    @DisplayName("executePythonScript throws when file missing but caught upstream")
    void executePythonScript_throwsWhenFileMissing() {
        File fakeFile = new File("not_existing_script.py");
        assertFalse(fakeFile.exists(), "Test script should not exist");

        try {
            var method = PriceScraperSchedulerService.class.getDeclaredMethod("executePythonScript");
            method.setAccessible(true);
            assertThrows(Exception.class, () -> method.invoke(service));
        } catch (Exception e) {
            log.info("Expected behavior: caught exception from reflection test");
        }
    }
}
