package com.nestuity.service.scheduler;

import com.nestuity.service.dto.BabyReminderDto;
import com.nestuity.service.entity.Baby;
import com.nestuity.service.entity.User;
import com.nestuity.service.service.*;
import com.nestuity.service.type.Frequency;
import com.nestuity.service.type.ReminderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ReminderScheduler {

    private final BabyService babyService;
    private final EmailService emailService;
    private final BabyReminderService babyReminderService;

    @Autowired
    public ReminderScheduler(BabyService babyService,
                             EmailService emailService,
                             BabyReminderService babyReminderService) {
        this.babyService = babyService;
        this.babyReminderService = babyReminderService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 0 * * 0") // Every Sunday at 00:00
    @Transactional
    public void sendWeeklyReport() {
        List<Baby> babies = babyService.getAllBabies();
        LocalDateTime now = LocalDateTime.now();

        for (Baby baby : babies) {
            User parent = baby.getUser();
            if (parent == null ||
                    parent.getPreferences() == null ||
                    !parent.getPreferences().isEmailNotificationsEnabled()) {
                continue;
            }

            try {
                List<BabyReminderDto> reminders = babyReminderService.getRemindersByBabyId(baby.getId());

                // --- ONLY OVERDUE TASKS ---
                List<Map<String, String>> overdueTasks = reminders.stream()
                        .filter(r -> r.getType() == ReminderType.TASK)
                        .filter(r -> r.getNextDue() != null)
                        .filter(r -> r.getFrequency() != Frequency.DAILY) // skip daily tasks
                        .filter(r -> r.getNextDue().isBefore(now)) // only past due dates
                        .map(r -> {
                            Map<String, String> task = new HashMap<>();
                            task.put("taskName", r.getTitle());
                            long daysOverdue = ChronoUnit.DAYS.between(r.getNextDue(), now);
                            task.put("daysOverdue", String.valueOf(daysOverdue));
                            return task;
                        })
                        .collect(Collectors.toList());

                // --- ONLY OVERDUE VACCINATIONS ---
                List<Map<String, String>> vaccinationsOverdue = reminders.stream()
                        .filter(r -> r.getType() == ReminderType.VACCINATION)
                        .filter(r -> r.getNextDue() != null && r.getNextDue().isBefore(now) && r.getCompletedOn() == null)
                        .map(r -> {
                            long daysOverdue = ChronoUnit.DAYS.between(r.getNextDue(), now);
                            return Map.of(
                                    "vaccinationName", r.getTitle(),
                                    "daysOverdue", String.valueOf(daysOverdue)
                            );
                        })
                        .collect(Collectors.toList());

                // --- SEND EMAIL ---
                emailService.sendWeeklySummaryEmail(
                        parent.getEmail(),
                        parent.getFirstName(),
                        overdueTasks,
                        vaccinationsOverdue
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
