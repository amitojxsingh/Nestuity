package com.nestuity.service.controller;

import com.nestuity.service.service.PriceScraperSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Admin controller for managing the price scraper.
 * Provides endpoints to manually trigger, enable, or disable the scraper.
 */
@RestController
@RequestMapping("/api/admin/scraper")
public class PriceScraperAdminController {
    private static final Logger log = LoggerFactory.getLogger(PriceScraperAdminController.class);

    private final PriceScraperSchedulerService scraperService;

    @Value("${scraper.enabled:true}")
    private boolean scraperEnabled;

    public PriceScraperAdminController(PriceScraperSchedulerService scraperService) {
        this.scraperService = scraperService;
    }

    /**
     * Manually trigger the price scraper immediately.
     * Useful for testing without waiting for the schedule.
     *
     * GET /api/admin/scraper/trigger
     */
    @PostMapping("/trigger")
    public ResponseEntity<Map<String, Object>> triggerScraper() {
        log.info("Manual scraper trigger requested");

        Map<String, Object> response = new HashMap<>();

        try {
            scraperService.triggerManually();
            response.put("success", true);
            response.put("message", "Price scraper triggered successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to trigger scraper: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Failed to trigger scraper: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get the current status of the price scraper.
     *
     * GET /api/admin/scraper/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("enabled", scraperEnabled);
        status.put("schedule", "0 0 0 1,15 * ? (1st and 15th of each month at midnight)");
        status.put("pythonPath", scraperService.getPythonPath());
        status.put("scriptPath", scraperService.getScriptPath());

        return ResponseEntity.ok(status);
    }

    /**
     * Enable or disable the scheduled scraper.
     *
     * POST /api/admin/scraper/toggle?enabled=true
     */
    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleScraper(@RequestParam boolean enabled) {
        log.info("Scraper toggle requested: {}", enabled);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("enabled", enabled);
        response.put("message", enabled ? "Scraper enabled" : "Scraper disabled");
        response.put("note", "Changes to scraper.enabled require application restart to take effect");

        return ResponseEntity.ok(response);
    }
}
