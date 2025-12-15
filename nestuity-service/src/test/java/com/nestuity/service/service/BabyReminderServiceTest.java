package com.nestuity.service.service;

import com.nestuity.service.dto.BabyReminderDto;
import com.nestuity.service.entity.Baby;
import com.nestuity.service.entity.BabyReminder;
import com.nestuity.service.loader.BabyReminderLoader;
import com.nestuity.service.repository.BabyReminderRepository;
import com.nestuity.service.repository.BabyRepository;
import com.nestuity.service.type.Frequency;
import com.nestuity.service.type.ReminderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BabyReminderServiceTest {

    @Mock private BabyReminderRepository reminderRepository;
    @Mock private BabyRepository babyRepository;
    @Mock private BabyReminderLoader reminderLoader;
    @InjectMocks private BabyReminderService babyReminderService;

    private Baby testBaby;
    private BabyReminder testReminder;

    @BeforeEach
    void setUp() {
        testBaby = new Baby();
        testBaby.setId(1L);
        testBaby.setDob(Date.from(LocalDate.now().minusDays(100).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        testReminder = new BabyReminder();
        testReminder.setId(10L);
        testReminder.setBaby(testBaby);
        testReminder.setTitle("Vaccination A");
        testReminder.setType(ReminderType.VACCINATION);
        testReminder.setFrequency(Frequency.MONTHLY);
        testReminder.setOccurrence(60);
        testReminder.setRequiresAction(true);
        testReminder.setNotes("Initial dose");
    }

    // ==================== CREATE TESTS ====================

    @Test
    void createReminder_savesLoadedRemindersWithoutDuplicates() {
        // Given
        List<BabyReminder> loaded = List.of(testReminder);
        when(babyRepository.findById(1L)).thenReturn(Optional.of(testBaby));
        when(reminderLoader.loadReminders()).thenReturn(loaded);
        when(reminderRepository.findByBabyId(1L)).thenReturn(List.of());
        when(reminderRepository.saveAll(anyList())).thenReturn(loaded);

        // When
        babyReminderService.createReminder(1L);

        // Then
        verify(reminderRepository).saveAll(argThat(iterable -> {
            List<BabyReminder> list = new ArrayList<>();
            iterable.forEach(list::add);
            return list.size() == 1 && list.getFirst().getBaby().equals(testBaby);
        }));
    }

    @Test
    void createReminder_doesNotSaveDuplicateTitles() {
        // Given
        when(babyRepository.findById(1L)).thenReturn(Optional.of(testBaby));
        when(reminderLoader.loadReminders()).thenReturn(List.of(testReminder));
        when(reminderRepository.findByBabyId(1L))
                .thenReturn(List.of(testReminder)); // duplicate already exists

        // When
        babyReminderService.createReminder(1L);

        // Then
        verify(reminderRepository, never()).saveAll(anyList());
    }

    @Test
    void createReminder_throwsIfBabyNotFound() {
        when(babyRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> babyReminderService.createReminder(999L));
    }

    // ==================== READ TESTS ====================

    @Test
    void getReminderById_returnsDtoIfFound() {
        when(reminderRepository.findById(10L)).thenReturn(Optional.of(testReminder));

        Optional<BabyReminderDto> result = babyReminderService.getReminderById(10L);

        assertTrue(result.isPresent());
        assertEquals(testReminder.getTitle(), result.get().title);
    }

    @Test
    void getRemindersByBabyId_returnsMappedDtos() {
        when(reminderRepository.findByBabyId(1L)).thenReturn(List.of(testReminder));

        List<BabyReminderDto> result = babyReminderService.getRemindersByBabyId(1L);

        assertEquals(1, result.size());
        assertEquals(testReminder.getTitle(), result.get(0).title);
    }

    // ==================== DATE-BASED FILTERS ====================

    @Test
    void getRemindersForToday_filtersDueTodayOrDaily() {
        BabyReminder dueToday = makeReminder(ReminderType.TASK, Frequency.DAILY, LocalDateTime.now().minusDays(1));
        when(reminderRepository.findByBabyId(1L)).thenReturn(List.of(dueToday));

        List<BabyReminderDto> result = babyReminderService.getRemindersForToday(1L);

        assertFalse(result.isEmpty());
    }

    @Test
    void getMedicalReminders_filtersVaccinationsWithin30Days() {
        BabyReminder vacc = makeReminder(ReminderType.VACCINATION, Frequency.ONCE,
                LocalDateTime.now().plusDays(10));
        when(reminderRepository.findByBabyId(1L)).thenReturn(List.of(vacc));

        List<BabyReminderDto> result = babyReminderService.getMedicalReminders(1L);
        assertEquals(1, result.size());
        assertEquals(ReminderType.VACCINATION, result.get(0).type);
    }

    @Test
    void getOverdueRecurring_returnsPastDueRecurringTasks() {
        BabyReminder pastDue = makeReminder(ReminderType.TASK, Frequency.DAILY,
                LocalDateTime.now().minusDays(2));
        when(reminderRepository.findByBabyId(1L)).thenReturn(List.of(pastDue));

        List<BabyReminderDto> result = babyReminderService.getOverdueRecurring(1L);
        assertEquals(1, result.size());
    }

    // ==================== UPDATE TESTS ====================

    public BabyReminderDto updateReminder(Long id, BabyReminderDto dto) {
        BabyReminder reminder = reminderRepository.findById(id)
                .orElseGet(() -> {
                    BabyReminder dummy = new BabyReminder();
                    dummy.setId(id);
                    return dummy;
                });

        if (dto.title != null) reminder.setTitle(dto.title);
        if (dto.notes != null) reminder.setNotes(dto.notes);

        BabyReminder saved = reminderRepository.save(reminder);

        BabyReminderDto result = new BabyReminderDto();
        result.title = saved.getTitle();
        result.notes = saved.getNotes();

        return result;
    }

    @Test
    void updateReminder_throwsIfNotFound() {
        when(reminderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () ->
                babyReminderService.updateReminder(999L, new BabyReminderDto()));
    }

    // ==================== DELETE TESTS ====================

    @Test
    void deleteReminder_deletesTaskType() {
        testReminder.setType(ReminderType.TASK);
        when(reminderRepository.findById(10L)).thenReturn(Optional.of(testReminder));

        babyReminderService.deleteReminder(10L);

        verify(reminderRepository).deleteById(10L);
    }

    @Test
    void deleteReminder_throwsIfNotTask() {
        testReminder.setType(ReminderType.MILESTONE);
        when(reminderRepository.findById(10L)).thenReturn(Optional.of(testReminder));

        assertThrows(IllegalStateException.class, () -> babyReminderService.deleteReminder(10L));
    }

    // ==================== MARK COMPLETE ====================

    @Test
    void markAsCompleted_updatesTimestampAndSaves() {
        testReminder.setType(ReminderType.TASK);
        when(reminderRepository.findById(10L)).thenReturn(Optional.of(testReminder));

        babyReminderService.markAsCompleted(10L, true);

        verify(reminderRepository).save(argThat(r -> r.getCompletedOn() != null));
    }

    @Test
    void markAsCompleted_throwsIfOnlyTasksAndNotTask() {
        testReminder.setType(ReminderType.VACCINATION);
        when(reminderRepository.findById(10L)).thenReturn(Optional.of(testReminder));
        assertThrows(IllegalStateException.class, () ->
                babyReminderService.markAsCompleted(10L, true));
    }

    // ==================== CURRENT MILESTONE ====================

    @Test
    void getCurrentMilestone_returnsClosestToAge() {
        testReminder.setType(ReminderType.MILESTONE);
        testReminder.setOccurrence(90);

        when(babyRepository.findById(1L)).thenReturn(Optional.of(testBaby));
        when(reminderRepository.findByBabyId(1L)).thenReturn(List.of(testReminder));

        Optional<BabyReminderDto> result = babyReminderService.getCurrentMilestone(1L);

        assertTrue(result.isPresent());
        assertEquals(ReminderType.MILESTONE, result.get().type);
    }

    @Test
    void getCurrentMilestone_returnsEmptyIfNoMilestones() {
        when(babyRepository.findById(1L)).thenReturn(Optional.of(testBaby));
        when(reminderRepository.findByBabyId(1L)).thenReturn(List.of());

        Optional<BabyReminderDto> result = babyReminderService.getCurrentMilestone(1L);
        assertTrue(result.isEmpty());
    }

    // ==================== UTIL ====================

    private BabyReminder makeReminder(ReminderType type, Frequency freq, LocalDateTime due) {
        BabyReminder r = new BabyReminder();
        r.setId(new Random().nextLong());
        r.setBaby(testBaby);
        r.setType(type);
        r.setTitle(type.name() + " Reminder");
        r.setFrequency(freq);
        r.setOccurrence(0);

        // FIX HERE — prevent NPE for DAILY reminders
        if (freq == Frequency.DAILY) {
            r.setCompletedOn(LocalDateTime.now().minusDays(1));
        } else {
            r.setCompletedOn(null);
        }

        if (due != null) {
            r.setOccurrence((int) Duration.between(
                    testBaby.getDob().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(),
                    due
            ).toDays());
        }

        return r;
    }
}
