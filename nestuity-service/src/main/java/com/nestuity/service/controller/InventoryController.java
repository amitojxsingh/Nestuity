package com.nestuity.service.controller;

import com.nestuity.service.entity.Inventory;
import com.nestuity.service.entity.User;
import com.nestuity.service.service.InventoryService;
import com.nestuity.service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final UserService userService;

    public InventoryController(InventoryService inventoryService, UserService userService) {
        this.inventoryService = inventoryService;
        this.userService = userService;
    }

    // ==================== CREATE ====================
    @PostMapping("/user/{userId}")
    public ResponseEntity<Inventory> createInventoryItem(
            @PathVariable Long userId,
            @RequestBody Inventory inventory
    ) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        inventory.setUser(userOpt.get());

        // Set default preferredSupplyMin if null
        if (inventory.getPreferredSupplyMin() == null) {
            inventory.setPreferredSupplyMin(14);
        }
        Inventory saved = inventoryService.saveInventory(inventory);
        return ResponseEntity.ok(saved);
    }

    // ==================== READ ====================

    // Get all inventory items
    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        List<Inventory> items = inventoryService.getAllInventory();
        return ResponseEntity.ok(items);
    }

    // Get inventory item by INVENTORY ID
    @GetMapping("/{inventoryId}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable Long inventoryId) {
        Inventory item = inventoryService.getInventoryById(inventoryId);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(item);
    }

    // Get all inventory items for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Inventory>> getInventoryByUser(@PathVariable Long userId) {
        try {
            List<Inventory> items = inventoryService.getInventoryByUserId(userId);
            return ResponseEntity.ok(items);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get a specific inventory item for a user by supply name
    @GetMapping("/user/{userId}/{supplyName}")
    public ResponseEntity<Inventory> getUserInventoryItem(
            @PathVariable Long userId,
            @PathVariable String supplyName
    ) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Inventory item = inventoryService.getInventoryItemByUserAndSupplyName(userId, supplyName);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get total SINGLE quantity of a given supply for a user (# of individual diapers)
    @GetMapping("/user/{userId}/{supplyName}/single-quantity")
    public ResponseEntity<Double> getTotalSingleQuantity(
            @PathVariable Long userId,
            @PathVariable String supplyName
    ) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Inventory item = inventoryService.getInventoryItemByUserAndSupplyName(userId, supplyName);
            if (item == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(item.getTotalSingleQuantity());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get total UNIT quantity of a given supply for a user (# of boxes)
    @GetMapping("/user/{userId}/{supplyName}/unit-quantity")
    public ResponseEntity<Double> getTotalUnitQuantity(
            @PathVariable Long userId,
            @PathVariable String supplyName
    ) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Inventory item = inventoryService.getInventoryItemByUserAndSupplyName(userId, supplyName);
            if (item == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(item.getTotalUnitQuantity());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== UPDATE ====================
    @PutMapping("/{inventoryId}")
    public ResponseEntity<Inventory> updateInventory(
            @PathVariable Long inventoryId,
            @RequestBody Inventory inventoryDetails
    ) {

        // Set default preferredSupplyMin if null
        if (inventoryDetails.getPreferredSupplyMin() == null) {
            inventoryDetails.setPreferredSupplyMin(14);
        }

        Inventory updated = inventoryService.updateInventory(inventoryId, inventoryDetails);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    // Update a user's inventory item SINGLE quantity by supply name
    @PutMapping("/user/{userId}/{supplyName}/single-quantity")
    public ResponseEntity<Inventory> updateSingleQuantity(
            @PathVariable Long userId,
            @PathVariable String supplyName,
            @RequestBody Map<String, Double> body // Expects { "totalSingleQuantity": 42.0 }
    ) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Double newQuantity = body.get("totalSingleQuantity");
        if (newQuantity == null || newQuantity < 0) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Inventory updatedItem = inventoryService.updateSingleItemQuantity(userId, supplyName, newQuantity);
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update a user's inventory item SINGLE quantity by supply name
    @PutMapping("/user/{userId}/{supplyName}/unit-quantity")
    public ResponseEntity<Inventory> updateUnitQuantity(
            @PathVariable Long userId,
            @PathVariable String supplyName,
            @RequestBody Map<String, Double> body // Expects { "totalUnitQuantity": 4.0 }
    ) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Double newUnitQuantity = body.get("totalUnitQuantity");
        if (newUnitQuantity == null || newUnitQuantity < 0) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Inventory updatedItem = inventoryService.updateUnitItemQuantity(userId, supplyName, newUnitQuantity);
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    // ==================== DELETE ====================
    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long inventoryId) {
        boolean deleted = inventoryService.deleteInventory(inventoryId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
