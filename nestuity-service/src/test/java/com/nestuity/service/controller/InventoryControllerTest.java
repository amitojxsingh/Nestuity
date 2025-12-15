package com.nestuity.service.controller;

import com.nestuity.service.entity.Inventory;
import com.nestuity.service.entity.User;
import com.nestuity.service.service.InventoryService;
import com.nestuity.service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private UserService userService;

    @InjectMocks
    private InventoryController inventoryController;

    private User user;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Napoleon");
        user.setLastName("Bonaparte");
        user.setEmail("napoleon@test.com");
        user.setActive(true);

        inventory = new Inventory();
        inventory.setSupplyName("diapers");
        inventory.setTotalSingleQuantity(50.0);
        inventory.setTotalUnitQuantity(5.0);
        inventory.setUnitConversion(10.0);
        inventory.setPreferredSupplyMin(null); // test default
    }

    @Test
    void createInventoryItem_SetsDefaultPreferredSupplyMin() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(inventoryService.saveInventory(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<Inventory> response = inventoryController.createInventoryItem(1L, inventory);

        assertEquals(200, response.getStatusCodeValue());
        Inventory savedInventory = response.getBody();
        assertNotNull(savedInventory);
        assertEquals(user, savedInventory.getUser());
        assertEquals("diapers", savedInventory.getSupplyName());
        // Default should be applied
        assertEquals(14, savedInventory.getPreferredSupplyMin());

        verify(inventoryService).saveInventory(any(Inventory.class));
    }

    @Test
    void createInventoryItem_UsesProvidedPreferredSupplyMin() {
        inventory.setPreferredSupplyMin(20);

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(inventoryService.saveInventory(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<Inventory> response = inventoryController.createInventoryItem(1L, inventory);

        Inventory savedInventory = response.getBody();
        assertNotNull(savedInventory);
        assertEquals(20, savedInventory.getPreferredSupplyMin());
    }

    @Test
    void createInventoryItem_UserNotFound_ReturnsNotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Inventory> response = inventoryController.createInventoryItem(1L, inventory);

        assertEquals(404, response.getStatusCodeValue());
        verify(inventoryService, never()).saveInventory(any());
    }
}
