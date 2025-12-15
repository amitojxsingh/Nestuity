package com.nestuity.service.service;

import com.nestuity.service.entity.Baby;
import com.nestuity.service.repository.BabyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BabyServiceTest {

    private BabyRepository babyRepository;
    private BabyService babyService;

    private Baby testBaby;

    @BeforeEach
    void setUp() {
        babyRepository = mock(BabyRepository.class);
        babyService = new BabyService(babyRepository);

        testBaby = new Baby();
        testBaby.setId(1L);
        testBaby.setName("Test Baby");
        testBaby.setWeight(3.5);
        testBaby.setDob(Date.valueOf("2025-01-01"));
        testBaby.setDiaperSize("1");
        testBaby.setDailyUsage(8);
    }

    // ==================== CREATE ====================
    @Test
    void createBaby_ShouldReturnSavedBaby() {
        when(babyRepository.save(testBaby)).thenReturn(testBaby);

        Baby saved = babyService.createBaby(testBaby);

        assertNotNull(saved);
        assertEquals("Test Baby", saved.getName());
        verify(babyRepository, times(1)).save(testBaby);
    }

    // ==================== READ ====================
    @Test
    void getAllBabies_ShouldReturnListOfBabies() {
        when(babyRepository.findAll()).thenReturn(Arrays.asList(testBaby));

        List<Baby> babies = babyService.getAllBabies();

        assertEquals(1, babies.size());
        assertEquals("Test Baby", babies.get(0).getName());
        verify(babyRepository, times(1)).findAll();
    }

    @Test
    void getBabyById_WhenExists_ShouldReturnBaby() {
        when(babyRepository.findById(1L)).thenReturn(Optional.of(testBaby));

        Optional<Baby> babyOpt = babyService.getBabyById(1L);

        assertTrue(babyOpt.isPresent());
        assertEquals("Test Baby", babyOpt.get().getName());
        verify(babyRepository, times(1)).findById(1L);
    }

    @Test
    void getBabyById_WhenNotExists_ShouldReturnEmpty() {
        when(babyRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Baby> babyOpt = babyService.getBabyById(999L);

        assertFalse(babyOpt.isPresent());
        verify(babyRepository, times(1)).findById(999L);
    }

    @Test
    void getBabiesByUserId_ShouldReturnBabies() {
        when(babyRepository.findByUserId(1L)).thenReturn(Arrays.asList(testBaby));

        List<Baby> babies = babyService.getBabiesByUserId(1L);

        assertEquals(1, babies.size());
        assertEquals("Test Baby", babies.get(0).getName());
        verify(babyRepository, times(1)).findByUserId(1L);
    }

    // ==================== UPDATE ====================
    @Test
    void updateBaby_ShouldReturnUpdatedBaby() {
        Baby updatedBaby = new Baby();
        updatedBaby.setId(1L);
        updatedBaby.setName("Updated Baby");
        updatedBaby.setWeight(4.0);

        when(babyRepository.save(updatedBaby)).thenReturn(updatedBaby);

        Baby result = babyService.updateBaby(updatedBaby);

        assertEquals("Updated Baby", result.getName());
        assertEquals(4.0, result.getWeight());
        verify(babyRepository, times(1)).save(updatedBaby);
    }

    // ==================== DELETE ====================
    @Test
    void deleteBaby_ShouldCallRepositoryDelete() {
        doNothing().when(babyRepository).deleteById(1L);

        babyService.deleteBaby(1L);

        verify(babyRepository, times(1)).deleteById(1L);
    }
}
