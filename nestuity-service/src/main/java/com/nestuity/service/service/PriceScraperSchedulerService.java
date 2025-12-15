package com.nestuity.service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Scheduled service that runs the Python price scraper every 2 weeks.
 */
@Service
public class PriceScraperSchedulerService {
    private static final Logger log = LoggerFactory.getLogger(PriceScraperSchedulerService.class);

    @Value("${scraper.python.path:/usr/bin/python3}")
    private String pythonPath;

    @Value("${scraper.script.path:/app/price-scraper/main.py}")
    private String scriptPath;

    @Value("${scraper.enabled:true}")
    private boolean scraperEnabled;

    // Getters for admin controller
    public String getPythonPath() {
        return pythonPath;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public boolean isScraperEnabled() {
        return scraperEnabled;
    }

    /**
     * Scheduled task that runs every 2 weeks at midnight.
     *
     * Note: Spring's cron doesn't support "every N days" directly.
     * Default runs on 1st and 15th of each month (approximately 2 weeks).
     *
     * Cron format: second minute hour day-of-month month day-of-week
     * "0 0 0 1,15 * ?" means: at 00:00:00 on the 1st and 15th of every month
     *
     * For true "every 14 days", use fixedDelay instead (14 days = 1209600000ms).
     */
    @Scheduled(cron = "${scraper.schedule.cron:0 0 0 1,15 * ?}")
    public void runPriceScraper() {
        if (!scraperEnabled) {
            log.info("Price scraper is disabled. Skipping execution.");
            return;
        }

        log.info("========================================");
        log.info("Starting scheduled price scraper");
        log.info("========================================");

        try {
            int exitCode = executePythonScript();

            if (exitCode == 0) {
                log.info("✓ Price scraper completed successfully");
            } else {
                log.error("✗ Price scraper failed with exit code: {}", exitCode);
            }
        } catch (Exception e) {
            log.error("Error running price scraper: {}", e.getMessage(), e);
        }

        log.info("========================================");
        log.info("Finished scheduled price scraper");
        log.info("========================================");
    }

    /**
     * Execute the Python scraper script and capture its output.
     *
     * @return Exit code from the Python script
     * @throws Exception if execution fails
     */
    private int executePythonScript() throws Exception {
        // Verify script exists
        File scriptFile = new File(scriptPath);
        if (!scriptFile.exists()) {
            throw new RuntimeException("Scraper script not found at: " + scriptPath);
        }

        log.info("Python path: {}", pythonPath);
        log.info("Script path: {}", scriptPath);

        // Build command
        List<String> command = new ArrayList<>();
        command.add(pythonPath);
        command.add(scriptPath);

        // Create process builder
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(scriptFile.getParentFile());  // Set working directory to script location
        processBuilder.redirectErrorStream(true);  // Merge stdout and stderr

        // Start process
        Process process = processBuilder.start();

        // Read output in real-time
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                log.info("[SCRAPER] {}", line);
            }
        }

        // Wait for process to complete
        int exitCode = process.waitFor();
        log.info("Scraper process exited with code: {}", exitCode);

        return exitCode;
    }

    /**
     * Manually trigger the price scraper (for testing purposes).
     * Can be called via an admin endpoint if needed.
     */
    public void triggerManually() {
        log.info("Manually triggering price scraper");
        runPriceScraper();
    }
}
