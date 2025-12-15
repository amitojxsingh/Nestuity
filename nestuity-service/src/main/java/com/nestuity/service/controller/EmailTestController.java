package com.nestuity.service.controller;

import com.nestuity.service.service.EmailService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * NOTE: This controller is never used in production.
 * It exists only for testing the email backend manually.
 */

@RestController
@RequestMapping("/email")
public class EmailTestController {

    private final EmailService emailService;

    public EmailTestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/welcome")
    public String sendWelcomeEmail(
            @RequestParam String to,
            @RequestParam(defaultValue = "Parent") String username) {
        try {
            emailService.sendWelcomeEmail(to, username);
            return "Welcome email sent successfully to " + to;
        } catch (Exception e) {
            return "Error sending email: " + e.getMessage();
        }
    }

    @GetMapping("/reminder")
    public String sendDiaperReminderEmail(
            @RequestParam String to,
            @RequestParam(defaultValue = "Parent") String username,
            @RequestParam Integer daysLeft) {
        try {
            emailService.sendDiaperReminderEmail(to, username, daysLeft);
            return "Diaper reminder email sent successfully to " + to;
        } catch (Exception e) {
            return "Error sending reminder email: " + e.getMessage();
        }
    }

    @GetMapping("/weekly-summary")
    public String sendWeeklySummaryEmail(
            @RequestParam String to,
            @RequestParam(defaultValue = "Parent") String username) {
        try {
            // Dummy data for testing with multiple overdue tasks and vaccinations
            List<Map<String, String>> overdueTasks = List.of(
                    Map.of("taskName", "Feed Baby", "daysOverdue", "2"),
                    Map.of("taskName", "Change Diaper", "daysOverdue", "1")
            );

            List<Map<String, String>> vaccinationsOverdue = List.of(
                    Map.of("vaccinationName", "MMR", "daysOverdue", "5")
            );

            emailService.sendWeeklySummaryEmail(to, username, overdueTasks, vaccinationsOverdue);
            return "Weekly summary email sent successfully to " + to;
        } catch (Exception e) {
            return "Error sending weekly summary email: " + e.getMessage();
        }
    }
}
