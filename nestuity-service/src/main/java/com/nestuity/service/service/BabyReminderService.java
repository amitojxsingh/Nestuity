package com.nestuity.service.service;

import com.nestuity.service.dto.BabyReminderDto;
import com.nestuity.service.entity.Baby;
import com.nestuity.service.entity.BabyReminder;
import com.nestuity.service.entity.UserPreferences;
import com.nestuity.service.loader.BabyReminderLoader;
import com.nestuity.service.repository.BabyReminderRepository;
import com.nestuity.service.repository.BabyRepository;
import com.nestuity.service.type.Frequency;
import com.nestuity.service.type.ReminderRange;
import com.nestuity.service.type.ReminderType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BabyReminderService {

    private final BabyReminderRepository reminderRepository;
    private final BabyRepository babyRepository;
    private final BabyReminderLoader babyReminderLoader;

    @Autowired
    public BabyReminderService(BabyReminderRepository reminderRepository, BabyReminderLoader babyReminderLoader, BabyRepository babyRepository) {
        this.reminderRepository = reminderRepository;
        this.babyReminderLoader = babyReminderLoader;
        this.babyRepository = babyRepository;
    }

    @Transactional
    public void createReminder(Long babyId) {
        Baby baby = babyRepository.findById(babyId)
                .orElseThrow(() -> new RuntimeException("Baby not found with id " + babyId));
        List<BabyReminder> reminders;
        reminders = babyReminderLoader.loadReminders();
        reminders.forEach(reminder -> reminder.setBaby(baby));
        List<String> existingReminders = reminderRepository.findByBabyId(babyId).stream()
                .map(BabyReminder::getTitle)
                .filter(Objects::nonNull)
                .toList();

        List<BabyReminder> toSave = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (BabyReminder reminder : reminders) {
            if (existingReminders.contains(reminder.getTitle())) {
                continue;
            }

            if (reminder.getType() == ReminderType.TASK && baby.getDob() != null) {
                LocalDate dob;
                if (baby.getDob() instanceof java.sql.Date sqlDate) {
                    dob = sqlDate.toLocalDate();
                } else {
                    dob = baby.getDob().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                }
                LocalDate lastCompletion = null;
                lastCompletion = computeLastCompletionDate(dob, today, reminder);
                if (lastCompletion != null && !lastCompletion.isAfter(today)) {
                    reminder.setCompletedOn(lastCompletion.atStartOfDay());
                }
            }
            toSave.add(reminder);
        }
        if (!toSave.isEmpty()) {
            reminderRepository.saveAll(toSave);
        }
    }

    public Optional<BabyReminderDto> getReminderById(Long id) {
        return reminderRepository.findById(id).map(this::toDtoWithNextDue);
    }

    public List<BabyReminderDto> getRemindersByBabyId(Long babyId) {
        return reminderRepository.findByBabyId(babyId).stream()
                .map(this::toDtoWithNextDue)
                .collect(Collectors.toList());
    }

    // Today's pending reminders
    public List<BabyReminderDto> getRemindersForToday(Long babyId) {
        LocalDate today = LocalDate.now();

        return getRemindersByBabyId(babyId).stream()
                .filter(dto -> {
                    if (dto.nextDue == null) return false;

                    boolean isDueTodayOrEarlier = !dto.nextDue.toLocalDate().isAfter(today);
                    boolean isDailyRecurring = dto.frequency == Frequency.DAILY;

                    // Show if:
                    // 1. It's due today or earlier (normal case), OR
                    // 2. It's a daily recurring task (always repeats)
                    return (isDueTodayOrEarlier || isDailyRecurring)
                            && !isCompletedAndNotDue(dto);
                })
                .collect(Collectors.toList());
    }


    // Upcoming X days (mutually exclusive ranges)
    public List<BabyReminderDto> getUpcomingReminders(Long babyId, Integer daysAhead) {
        if (babyId == null) {
            throw new IllegalArgumentException("babyId cannot be null");
        }
        // Fetch reminders for this baby
        List<BabyReminderDto> reminders = getRemindersByBabyId(babyId); // reuse your existing method
        if (reminders == null || reminders.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Baby with ID " + babyId + " not found");
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = (daysAhead != null && daysAhead >= 0) ? today.plusDays(daysAhead) : null;

        return reminders.stream()
                .filter(dto -> dto.nextDue != null)  // skip reminders with no due date
                .filter(dto -> dto.type != ReminderType.MILESTONE) // exclude milestones
                .peek(dto -> {
                    LocalDate dueDate = dto.nextDue.toLocalDate();
                    LocalDate completedOn = dto.completedOn != null ? dto.completedOn.toLocalDate() : null;
                    ReminderRange tag;

                    if (!dto.isUserCreated() && completedOn != null
                            && (!dueDate.isAfter(completedOn) || completedOn.isEqual(today))) {
                        tag = ReminderRange.COMPLETED;
                    } else if (dueDate.isBefore(today)) {
                        tag = ReminderRange.OVERDUE;
                    } else if (dueDate.isEqual(today)) {
                        tag = ReminderRange.TODAY;
                    } else if (!dueDate.isAfter(today.plusDays(7))) {
                        tag = ReminderRange.UPCOMING_WEEK;
                    } else if (!dueDate.isAfter(today.plusDays(30))) {
                        tag = ReminderRange.UPCOMING_MONTH;
                    } else {
                        tag = ReminderRange.FUTURE;
                    }

                    dto.setRange(tag);
                })
                .filter(dto -> {
                    if (endDate == null) return true;
                    LocalDate dueDate = dto.nextDue.toLocalDate();
                    return !dueDate.isBefore(today) && !dueDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    // Medical reminders (vaccination/checkup) within 30 days
    public List<BabyReminderDto> getMedicalReminders(Long babyId) {
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(30);
        return getRemindersByBabyId(babyId).stream()
                .filter(dto -> dto.type == ReminderType.VACCINATION)
                .filter(dto -> dto.nextDue != null)
                .filter(dto -> {
                    LocalDate d = dto.nextDue.toLocalDate();
                    return (d.isAfter(today) || d.isEqual(today)) && d.isBefore(end.plusDays(1));
                })
                .collect(Collectors.toList());
    }

    // Overdue recurring tasks: recurrence computed and nextDue < now and not completed (or completed but nextDue <= now)
    public List<BabyReminderDto> getOverdueRecurring(Long babyId) {
        LocalDateTime now = LocalDateTime.now();
        return getRemindersByBabyId(babyId).stream()
                .filter(dto -> dto.frequency != Frequency.DAILY || dto.type != ReminderType.MILESTONE)
                .filter(dto -> dto.nextDue != null && dto.nextDue.isBefore(now))
                .collect(Collectors.toList());
    }

    public Optional<BabyReminderDto> getCurrentMilestone(Long babyId) {
        // Fetch baby and reminders
        Baby baby = babyRepository.findById(babyId)
                .orElseThrow(() -> new RuntimeException("Baby not found with id " + babyId));
        LocalDate dob = baby.getDob()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        long babyAgeDays = Duration.between(dob.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays();
        // Get all milestone reminders
        List<BabyReminder> milestones = reminderRepository.findByBabyId(babyId).stream()
                .filter(r -> r.getType() == ReminderType.MILESTONE)
                .toList();
        if (milestones.isEmpty()) return Optional.empty();
        // Find the milestone with occurrence <= babyAgeDays (closest to current age)
        BabyReminder current = milestones.stream()
                .filter(r -> r.getOccurrence() != null && r.getOccurrence() <= babyAgeDays)
                .max((a, b) -> Integer.compare(a.getOccurrence(), b.getOccurrence()))
                .orElse(null);
        if (current == null) return Optional.empty();
        return Optional.of(toDtoWithNextDue(current));
    }

    @Transactional
    public BabyReminderDto createTaskReminder(Long babyId, BabyReminderDto newTaskReminder) {
        // Only allow creating regular TASK reminders
        if (newTaskReminder.type == ReminderType.MILESTONE) {
            throw new RuntimeException("Cannot manually create milestone reminders.");
        }

        Baby baby = babyRepository.findById(babyId)
                .orElseThrow(() -> new RuntimeException("Baby not found with ID: " + babyId));

        BabyReminder reminder = new BabyReminder();
        reminder.setBaby(baby);
        reminder.setType(newTaskReminder.type);
        reminder.setTitle(newTaskReminder.title);
        reminder.setDescription(newTaskReminder.description);
        reminder.setFrequency(newTaskReminder.frequency);
        reminder.setOccurrence(newTaskReminder.occurrence);
        reminder.setRequiresAction(true);
        reminder.setNotes(newTaskReminder.notes);
        reminder.setUserCreated(true);
        BabyReminder saved = reminderRepository.save(reminder);

        // Handle start date and completedOn logic
        if (newTaskReminder.getStartDate() != null) {
            // Adding Vaccination
            if (newTaskReminder.type == ReminderType.VACCINATION) {
                Date dob = baby.getDob();
                LocalDateTime startDateTime = newTaskReminder.getStartDate();
                if (dob != null && startDateTime != null) {
                    LocalDate dobLocal = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate startDate = startDateTime.toLocalDate();
                    long daysSinceBirth = ChronoUnit.DAYS.between(dobLocal, startDate);
                    reminder.setOccurrence((int) daysSinceBirth);
                }
            }
            else
            {
                // Adding Task
                if (newTaskReminder.getFrequency() != null) {
                    UserPreferences prefs = baby.getUser().getPreferences();
                    String timezone = (prefs != null) ? prefs.getTimezone() : null;
                    LocalDateTime lastCompleted = computeLastCompletedBeforeNowFromStart(
                            newTaskReminder.getStartDate(),
                            newTaskReminder.getFrequency(),
                            safeZone(timezone)
                    );
                    if (newTaskReminder.getFrequency() != Frequency.ONCE) {
                        reminder.setCompletedOn(lastCompleted);
                    } else {
                        Date dob = baby.getDob();
                        LocalDate localDob = dob.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        LocalDate startDate = newTaskReminder.getStartDate().toLocalDate();
                        int daysBetween = (int) ChronoUnit.DAYS.between(localDob, startDate);
                        reminder.setOccurrence(daysBetween);
                    }
                    reminderRepository.save(reminder);
                }
            }
        }

        return toDtoWithNextDue(reminder);
    }


    @Transactional
    public BabyReminderDto updateReminder(Long id, BabyReminderDto updated) {
        BabyReminder saved = reminderRepository.findById(id)
                .map(existing -> {
                    if (existing.getType() == ReminderType.MILESTONE) {
                        throw new RuntimeException("Cannot update a milestone reminder.");
                    }

                    if (updated.title != null) existing.setTitle(updated.title);
                    if (updated.description != null) existing.setDescription(updated.description);
                    if (updated.frequency != null) existing.setFrequency(updated.frequency);
                    if (updated.occurrence != null) existing.setOccurrence(updated.occurrence);
                    if (updated.requiresAction != null) existing.setRequiresAction(updated.requiresAction);
                    if (updated.notes != null) existing.setNotes(updated.notes);

                    // Recalculate completedOn so nextDue lines up with the requested startDate.
                    // VACCINATION
                    if (updated.type == ReminderType.VACCINATION) {
                        Date dob = existing.getBaby().getDob();
                        LocalDateTime startDateTime = updated.getStartDate();
                        if (dob != null && startDateTime != null) {
                            LocalDate dobLocal = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            LocalDate startDate = startDateTime.toLocalDate();
                            long daysSinceBirth = ChronoUnit.DAYS.between(dobLocal, startDate);
                            existing.setOccurrence((int) daysSinceBirth);
                        }
                    }
                    // TASK
                    else {
                        if (updated.startDate != null && updated.frequency != null && updated.frequency != Frequency.ONCE) {
                            LocalDateTime start = updated.startDate;
                            UserPreferences prefs = existing.getBaby().getUser().getPreferences();
                            String timezone = (prefs != null) ? prefs.getTimezone() : null;
                            LocalDateTime lastCompleted = computeLastCompletedBeforeNowFromStart(
                                    start,
                                    updated.frequency,
                                    safeZone(timezone)
                            );
                            // For ONCE we typically don't set a completedOn (single due date).
                            existing.setCompletedOn(lastCompleted);
                        }
                        // Compute occurrence as days from baby's DOB to startDate
                        if (updated.frequency == Frequency.ONCE) {
                            LocalDate dob = existing.getBaby().getDob().toInstant()
                                    .atZone(ZoneId.systemDefault()).toLocalDate();
                            LocalDate start = updated.startDate.toLocalDate();
                            int daysBetween = (int) ChronoUnit.DAYS.between(dob, start);
                            existing.setOccurrence(daysBetween);
                        }
                    }
                    existing.setUserCreated(true);
                    return reminderRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Reminder not found with id " + id));
        return toDtoWithNextDue(saved);
    }

    @Transactional
    public void deleteReminder(Long id) {
        BabyReminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found with id " + id));

        if (reminder.getType() != ReminderType.TASK
                && reminder.getType() != ReminderType.VACCINATION) {
            throw new IllegalStateException("Only reminders of type TASK or VACCINATION can be deleted.");
        }
        reminderRepository.deleteById(id);
    }

    @Transactional
    public void markAsCompleted(Long id, boolean onlyTasks) {
        BabyReminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found with id " + id));
        if (onlyTasks && reminder.getType() != ReminderType.TASK) {
            throw new IllegalStateException("Only reminders of type TASK can perform this action.");
        }
        // set completed timestamp
        LocalDateTime now = LocalDateTime.now();
        reminder.setCompletedOn(now);
        reminder.setUserCreated(false);

        reminderRepository.save(reminder);
    }

    // conversion helper
    private BabyReminderDto toDtoWithNextDue(BabyReminder reminder) {
        BabyReminderDto dto = new BabyReminderDto();
        dto.id = reminder.getId();
        dto.babyId = reminder.getBaby() != null ? reminder.getBaby().getId() : null;
        dto.type = reminder.getType();
        dto.title = reminder.getTitle();
        dto.description = reminder.getDescription();
        dto.frequency = reminder.getFrequency();
        dto.occurrence = reminder.getOccurrence();
        dto.requiresAction = reminder.getRequiresAction();
        dto.notes = reminder.getNotes();
        dto.completedOn = reminder.getCompletedOn();
        dto.nextDue = estimateNextDue(reminder);
        dto.userCreated = reminder.isUserCreated();
        return dto;
    }

    private boolean isCompletedAndNotDue(BabyReminderDto dto) {
        if (dto.completedOn == null) return false;
        if (dto.nextDue == null) return false;
        return dto.nextDue.isAfter(LocalDateTime.now());
    }

    private LocalDate computeLastCompletionDate(LocalDate dob, LocalDate today, BabyReminder reminder) {
        if (dob == null || today == null || reminder == null) return null;

        // Use occurrence when present (days). If missing, fall back to defaults.
        int occurrenceDays = (reminder.getOccurrence() != null) ? reminder.getOccurrence() : 0;
        Frequency freq = reminder.getFrequency();

        // If occurrenceDays is zero, pick a sensible default mapping
        if (occurrenceDays <= 0) {
            if (freq == null) return null;
            switch (freq) {
                case DAILY -> occurrenceDays = 1;
                case WEEKLY -> occurrenceDays = 7;
                case MONTHLY -> occurrenceDays = 30;
                case QUARTERLY -> occurrenceDays = 90;
                case ANNUAL -> occurrenceDays = 365;
                default -> occurrenceDays = 0;
            }
        }

        if (occurrenceDays <= 0) return null;

        long daysSinceDob = ChronoUnit.DAYS.between(dob, today);
        if (daysSinceDob <= 0) {
            // baby not born yet or born today -> no past completions
            return null;
        }

        // How many full cycles (intervals) have passed since dob?
        long fullCycles = daysSinceDob / occurrenceDays;

        // If no cycles completed, nothing to mark
        if (fullCycles <= 0) {
            return null;
        }

        LocalDate lastCompletion = dob.plusDays(fullCycles * (long) occurrenceDays);

        // Ensure lastCompletion is strictly before today (we want "past" completion)
        if (!lastCompletion.isBefore(today)) {
            // If the calculated lastCompletion equals today, step back one cycle
            lastCompletion = lastCompletion.minusDays(occurrenceDays);
        }

        // final sanity: lastCompletion must be >= dob
        if (lastCompletion.isBefore(dob)) return null;

        // Return lastCompletion only if it's before today
        return lastCompletion.isBefore(today) ? lastCompletion : null;
    }

    private LocalDateTime estimateNextDue(BabyReminder reminder) {
        if (reminder == null) return null;

        // Ensure we have the necessary context
        LocalDate dob = null;
        if (reminder.getBaby() != null && reminder.getBaby().getDob() != null) {
            dob = reminder.getBaby().getDob()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        // Directly read numeric occurrence (default to 0)
        int days = (reminder.getOccurrence() != null) ? reminder.getOccurrence() : 0;
        Frequency freq = reminder.getFrequency();
        LocalDateTime completed = reminder.getCompletedOn();

        switch (reminder.getType()) {

            case MILESTONE -> {
                // Milestone: only one date based on DOB + occurrence
                if (dob != null)
                    return LocalDateTime.of(dob.plusDays(days), LocalTime.MIDNIGHT);
            }

            case VACCINATION -> {
                if (dob == null) break;
                LocalDateTime dueDate = LocalDateTime.of(dob.plusDays(days), LocalTime.MIDNIGHT);
                if (freq == Frequency.ONCE) {
                    // one-time vaccines do not repeat
                    return dueDate;
                }
                if (completed != null && freq != null)
                    return applyFrequencyOffset(freq, completed);
                return dueDate;
            }

            case TASK -> {
                if (freq == null) break;
                // Daily tasks are based on current date rather than DOB
                if (freq == Frequency.DAILY) {
                    LocalDateTime nextDueDateTime;
                    LocalDate today = LocalDate.now();
                    if (completed.toLocalDate().isAfter(today) && reminder.isUserCreated()) {
                        nextDueDateTime = completed.toLocalDate().plusDays(1).atStartOfDay();
                    }
                    else if (completed.toLocalDate().isEqual(today)) {
                        // Completed today → next due tomorrow
                        nextDueDateTime = LocalDateTime.of(today.plusDays(1), LocalTime.MIDNIGHT);
                    } else {
                        // Not completed today → next due today
                        nextDueDateTime = LocalDateTime.of(today, LocalTime.MIDNIGHT);
                    }
                    return nextDueDateTime;
                }

                // For other repeating frequencies, use DOB + occurrence
                LocalDateTime base = (dob != null)
                        ? LocalDateTime.of(dob.plusDays(days), LocalTime.MIDNIGHT)
                        : LocalDateTime.now().plusDays(days);

                // One-time tasks (Frequency.ONCE) shouldn't repeat
                if (freq == Frequency.ONCE) {
                    return base;
                }

                // If completed, schedule next based on frequency
                if (completed != null)
                    return applyFrequencyOffset(freq, completed);

                return base;
            }

            default -> {}
        }

        return null;
    }

    /**
     * Returns the last completed occurrence BEFORE now derived from the anchor `start`.
     * If frequency == ONCE this returns null (no repeating history).
     */
    private LocalDateTime computeLastCompletedBeforeNowFromStart(LocalDateTime start, Frequency freq, ZoneId zoneId) {
        if (start == null || freq == null || freq == Frequency.ONCE) return null;

        // Use the chosen timezone for "now"
        ZonedDateTime nowZoned = ZonedDateTime.now(zoneId);
        LocalDateTime now = nowZoned.toLocalDateTime();
        LocalDateTime nextOccurrence = start;

        // advance until nextOccurrence is strictly after now
        while (!nextOccurrence.isAfter(now)) {
            nextOccurrence = addInterval(nextOccurrence, freq, 1);
        }

        // lastCompleted = previous occurrence
        LocalDateTime lastCompleted = addInterval(nextOccurrence, freq, -1);

        // normalize to start of day
        return lastCompleted.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }


    /** addInterval supports negative steps too (so subtraction works). */
    private LocalDateTime addInterval(LocalDateTime dt, Frequency freq, long step) {
        return switch (freq) {
            case DAILY -> dt.plusDays(step);
            case WEEKLY -> dt.plusWeeks(step);
            case MONTHLY -> dt.plusMonths(step);
            case QUARTERLY -> dt.plusMonths(step * 3);
            case ANNUAL -> dt.plusYears(step);
            default -> dt;
        };
    }
    /**
     * Apply frequency-based offset to a base date (usually completedOn).
     */
    private LocalDateTime applyFrequencyOffset(Frequency frequency, LocalDateTime base) {
        return switch (frequency) {
            case DAILY -> base.plusDays(1);
            case WEEKLY -> base.plusWeeks(1);
            case MONTHLY -> base.plusMonths(1);
            case QUARTERLY -> base.plusMonths(3);
            case ANNUAL -> base.plusYears(1);
            default -> base;
        };
    }

    private ZoneId safeZone(String timezone) {
        try {
            return (timezone != null && !timezone.isBlank()) ? ZoneId.of(timezone)
                    : ZoneId.of("America/Edmonton");
        } catch (DateTimeException e) {
            return ZoneId.of("America/Edmonton");
        }
    }
}
