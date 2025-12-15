package com.nestuity.service.service;

import com.nestuity.service.entity.Inventory;
import com.nestuity.service.entity.User;
import com.nestuity.service.repository.InventoryRepository;
import com.nestuity.service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private User user;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Jane");
        user.setLastName("Doe");

        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setSupplyName("diapers");
        inventory.setTotalSingleQuantity(50.0);
        inventory.setTotalUnitQuantity(5.0);
        inventory.setUnitConversion(10.0);
        inventory.setUser(user);
    }

    // ---------------- CREATE ----------------
    @Test
    void testSaveInventory_Success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        Inventory saved = inventoryService.saveInventory(inventory);

        assertNotNull(saved);
        assertEquals(user, saved.getUser());
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void testSaveInventory_UserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> inventoryService.saveInventory(inventory));
        assertTrue(ex.getMessage().contains("User not found"));
        verify(inventoryRepository, never()).save(any());
    }

    // ---------------- READ ----------------
    @Test
    void testGetAllInventory() {
        when(inventoryRepository.findAll()).thenReturn(Collections.singletonList(inventory));

        List<Inventory> result = inventoryService.getAllInventory();
        assertEquals(1, result.size());
        verify(inventoryRepository).findAll();
    }

    @Test
    void testGetInventoryById_Found() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        Inventory result = inventoryService.getInventoryById(1L);
        assertEquals(inventory, result);
    }

    @Test
    void testGetInventoryById_NotFound() {
        when(inventoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertNull(inventoryService.getInventoryById(2L));
    }

    @Test
    void testGetInventoryByUserId_Success() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(inventoryRepository.findByUserId(user.getId())).thenReturn(Collections.singletonList(inventory));

        List<Inventory> result = inventoryService.getInventoryByUserId(user.getId());
        assertEquals(1, result.size());
        verify(inventoryRepository).findByUserId(user.getId());
    }

    @Test
    void testGetInventoryByUserId_UserNotFound() {
        when(userRepository.existsById(user.getId())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> inventoryService.getInventoryByUserId(user.getId()));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void testGetInventoryItemByUserAndSupplyName_Success() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(inventoryRepository.findByUserIdAndSupplyNameIgnoreCase(user.getId(), "diapers"))
                .thenReturn(Optional.of(inventory));

        Inventory result = inventoryService.getInventoryItemByUserAndSupplyName(user.getId(), "diapers");
        assertEquals(inventory, result);
    }

    @Test
    void testGetInventoryItemByUserAndSupplyName_NotFound() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(inventoryRepository.findByUserIdAndSupplyNameIgnoreCase(user.getId(), "wipes"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                inventoryService.getInventoryItemByUserAndSupplyName(user.getId(), "wipes"));
        assertTrue(ex.getMessage().contains("No inventory item found"));
    }

    @Test
    void testGetTotalItemQuantity() {
        Inventory other = new Inventory();
        other.setSupplyName("wipes");
        other.setTotalSingleQuantity(20.0);
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(inventoryRepository.findByUserId(user.getId())).thenReturn(Arrays.asList(inventory, other));

        double total = inventoryService.getTotalItemQuantity(user.getId(), "diapers");
        assertEquals(50.0, total);
    }

    // ---------------- UPDATE ----------------
    @Test
    void testSetSingleItemQuantity() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(inventoryRepository.findByUserIdAndSupplyNameIgnoreCase(user.getId(), "diapers"))
                .thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        Inventory updated = inventoryService.setSingleItemQuantity(user.getId(), "diapers", 100.0);
        assertEquals(100.0, updated.getTotalSingleQuantity());
    }

    @Test
    void testUpdateInventory() {
        Inventory newDetails = new Inventory();
        newDetails.setSupplyName("wipes");
        newDetails.setTotalSingleQuantity(30.0);
        newDetails.setTotalUnitQuantity(3.0);
        newDetails.setUnitConversion(10.0);
        newDetails.setUser(user);

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        Inventory result = inventoryService.updateInventory(1L, newDetails);

        assertEquals("wipes", result.getSupplyName());
        assertEquals(30, result.getTotalSingleQuantity());
        verify(inventoryRepository).save(any());
    }

    // ---------------- DELETE ----------------
    @Test
    void testDeleteInventory_Exists() {
        when(inventoryRepository.existsById(1L)).thenReturn(true);
        boolean deleted = inventoryService.deleteInventory(1L);
        assertTrue(deleted);
        verify(inventoryRepository).deleteById(1L);
    }

    @Test
    void testDeleteInventory_NotExists() {
        when(inventoryRepository.existsById(2L)).thenReturn(false);
        boolean deleted = inventoryService.deleteInventory(2L);
        assertFalse(deleted);
        verify(inventoryRepository, never()).deleteById(2L);
    }
}
