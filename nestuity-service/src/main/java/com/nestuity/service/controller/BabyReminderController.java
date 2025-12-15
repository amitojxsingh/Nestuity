package com.nestuity.service.controller;

import com.nestuity.service.dto.BabyReminderDto;
import com.nestuity.service.entity.BabyReminder;
import com.nestuity.service.service.BabyReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reminders")
public class BabyReminderController {

    private final BabyReminderService reminderService;

    @Autowired
    public BabyReminderController(BabyReminderService reminderService) {
        this.reminderService = reminderService;
    }

    // Create a set of baseline reminders for a baby (loads from JSON and attaches baby)
    @PostMapping("/{babyId}")
    public ResponseEntity<Void> createRemindersForBaby(@PathVariable Long babyId) {
        reminderService.createReminder(babyId);
        return ResponseEntity.ok().build();
    }

    // Get a specific reminder by its ID
    @GetMapping("/{id}")
    public ResponseEntity<BabyReminderDto> getReminderById(@PathVariable Long id) {
        return reminderService.getReminderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all reminders for a baby (returns computed DTOs with nextDue)
    @GetMapping("/baby/{babyId}")
    public ResponseEntity<List<BabyReminderDto>> getRemindersByBabyId(@PathVariable Long babyId) {
        return ResponseEntity.ok(reminderService.getRemindersByBabyId(babyId));
    }

    // Get upcoming reminders (today, week, or month) — flexible via query param
    @GetMapping("/baby/{babyId}/upcoming")
    public ResponseEntity<List<BabyReminderDto>> getUpcomingReminders(
            @PathVariable Long babyId,
            @RequestParam(required = false) Integer daysAhead) {
        return ResponseEntity.ok(reminderService.getUpcomingReminders(babyId, daysAhead));
    }

    // Get reminders by category (medical / overdue)
    @GetMapping("/baby/{babyId}/reminders")
    public ResponseEntity<List<BabyReminderDto>> getRemindersByType(
            @PathVariable Long babyId,
            @RequestParam String type) {

        return switch (type.toLowerCase()) {
            case "medical" -> ResponseEntity.ok(reminderService.getMedicalReminders(babyId));
            case "overdue" -> ResponseEntity.ok(reminderService.getOverdueRecurring(babyId));
            default -> ResponseEntity.badRequest().build();
        };
    }

    // Create a custom task reminder for a baby
    @PostMapping("/baby/{babyId}/task")
    public ResponseEntity<BabyReminderDto> createTaskReminder(
            @PathVariable Long babyId,
            @RequestBody BabyReminderDto newTaskReminder
    ) {
        BabyReminderDto created = reminderService.createTaskReminder(babyId, newTaskReminder);
        return ResponseEntity.ok(created);
    }

    // Update an existing reminder (partial updates supported)
    @PutMapping("/{id}")
    public ResponseEntity<BabyReminderDto> updateReminder(
            @PathVariable Long id,
            @RequestBody BabyReminderDto updatedReminder
    ) {
        return ResponseEntity.ok(reminderService.updateReminder(id, updatedReminder));
    }

    // Mark a reminder as completed (normal completion)
    @PutMapping("/{id}/complete")
    public ResponseEntity<Void> completeReminder(@PathVariable Long id) {
        reminderService.markAsCompleted(id, false);
        return ResponseEntity.ok().build();
    }

    // Delete-style operation — marks TASK reminders as completed (task-only)
    @DeleteMapping("/{id}/task-complete")
    public ResponseEntity<Void> completeTaskReminder(@PathVariable Long id) {
        reminderService.markAsCompleted(id, true);
        return ResponseEntity.ok().build();
    }

    // Permanently delete a reminder (must be TASK type)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id) {
        reminderService.deleteReminder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/baby/{babyId}/current")
    public ResponseEntity<BabyReminderDto> getCurrentMilestone(@PathVariable Long babyId) {
        BabyReminderDto milestone = reminderService.getCurrentMilestone(babyId)
                .orElse(new BabyReminderDto()); // empty DTO
        return ResponseEntity.ok(milestone);
    }

}
