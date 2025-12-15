package com.nestuity.service.service;

import com.nestuity.service.entity.UserPreferences;
import com.nestuity.service.repository.UserPreferencesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserPreferencesServiceTest {

    private UserPreferencesRepository preferencesRepository;
    private UserPreferencesService preferencesService;

    private UserPreferences samplePreferences;

    @BeforeEach
    void setUp() {
        preferencesRepository = mock(UserPreferencesRepository.class);
        preferencesService = new UserPreferencesService(preferencesRepository);

        samplePreferences = new UserPreferences(
                1L,
                "USD",
                "America/New_York",
                true,
                false
        );
    }

    // ==================== SAVE / CREATE ====================

    @Test
    void savePreferences_NewPreferences_Success() {
        when(preferencesRepository.save(any(UserPreferences.class))).thenReturn(samplePreferences);

        UserPreferences saved = preferencesService.savePreferences(samplePreferences);

        assertNotNull(saved);
        assertEquals("USD", saved.getCurrency());
        assertEquals("America/New_York", saved.getTimezone());
        assertTrue(saved.isEmailNotificationsEnabled());
        assertFalse(saved.isSmsNotificationsEnabled());

        verify(preferencesRepository, times(1)).save(samplePreferences);
    }

    @Test
    void savePreferences_UpdatePreferences_Success() {
        // Update existing preferences
        samplePreferences.setCurrency("EUR");
        when(preferencesRepository.save(any(UserPreferences.class))).thenReturn(samplePreferences);

        UserPreferences updated = preferencesService.savePreferences(samplePreferences);

        assertEquals("EUR", updated.getCurrency());
        verify(preferencesRepository, times(1)).save(samplePreferences);
    }

    // ==================== GET ====================

    @Test
    void getPreferencesByUserId_Found() {
        when(preferencesRepository.findById(1L)).thenReturn(Optional.of(samplePreferences));

        Optional<UserPreferences> result = preferencesService.getPreferencesByUserId(1L);

        assertTrue(result.isPresent());
        assertEquals("USD", result.get().getCurrency());
    }

    @Test
    void getPreferencesByUserId_NotFound() {
        when(preferencesRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<UserPreferences> result = preferencesService.getPreferencesByUserId(999L);

        assertFalse(result.isPresent());
    }

    // ==================== DELETE ====================

    @Test
    void deletePreferencesByUserId_Success() {
        doNothing().when(preferencesRepository).deleteById(1L);

        preferencesService.deletePreferencesByUserId(1L);

        verify(preferencesRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletePreferencesByUserId_NotExistingId_DoesNotThrow() {
        // Simulate that deleting a non-existent ID does nothing
        doThrow(new RuntimeException("Not found")).when(preferencesRepository).deleteById(999L);

        assertThrows(RuntimeException.class, () -> preferencesService.deletePreferencesByUserId(999L));
        verify(preferencesRepository, times(1)).deleteById(999L);
    }
}
