package com.nestuity.service.service;

import com.nestuity.service.entity.Inventory;
import com.nestuity.service.entity.User;
import com.nestuity.service.repository.InventoryRepository;
import com.nestuity.service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    public InventoryService(InventoryRepository inventoryRepository, UserRepository userRepository) {
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
    }

    // ---------------- CREATE ----------------
    public Inventory saveInventory(Inventory inventory) {
        Long userId = inventory.getUser().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        inventory.setUser(user);
        return inventoryRepository.save(inventory);
    }

    // ---------------- READ ----------------

    // Get ALL of the Inventory
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    // Get a specific inventory using the inventory ID
    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id).orElse(null);
    }

    // Get a specific USER'S inventory using their USER ID
    public List<Inventory> getInventoryByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        return inventoryRepository.findByUserId(userId);
    }

    // Get a user's specific inventory item using their USER ID and the supply name
    public Inventory getInventoryItemByUserAndSupplyName(Long userId, String supplyName) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        return inventoryRepository.findByUserIdAndSupplyNameIgnoreCase(userId, supplyName)
                .orElseThrow(() -> new RuntimeException(
                        "No inventory item found for user ID " + userId + " with supply '" + supplyName + "'"
                ));
    }

    // Get a user's total quantity of a specific supply using their USER ID and SUPPLY NAME
    public double getTotalItemQuantity(Long userId, String... supplyNames) {
        List<Inventory> inventoryList = getInventoryByUserId(userId);
        return inventoryList.stream()
                .filter(item -> {
                    for (String name : supplyNames) {
                        if (item.getSupplyName().equalsIgnoreCase(name)) {
                            return true;
                        }
                    }
                    return false;
                })
                .mapToDouble(Inventory::getTotalSingleQuantity)
                .sum();
    }
    // Get a user's total quantity of a specific supply using their USER ID and SUPPLY NAME
    public double getTotalUnitQuantity(Long userId, String... supplyNames) {
        List<Inventory> inventoryList = getInventoryByUserId(userId);
        return inventoryList.stream()
                .filter(item -> {
                    for (String name : supplyNames) {
                        if (item.getSupplyName().equalsIgnoreCase(name)) {
                            return true;
                        }
                    }
                    return false;
                })
                .mapToDouble(Inventory::getTotalUnitQuantity)
                .sum();
    }

    // ---------------- UPDATE ----------------

    // Set the quantity of the TOTAL SINGLE QUANTITY
    public Inventory setSingleItemQuantity(Long userId, String supplyName, double quantity) {
        Inventory item = getInventoryItemByUserAndSupplyName(userId, supplyName);
        item.setTotalSingleQuantity(quantity);
        return inventoryRepository.save(item);
    }

    // Set the quantity of the TOTAL UNIT QUANITTY
    public Inventory setUnitQuantity(Long userId, String supplyName, double quantity) {
        Inventory item = getInventoryItemByUserAndSupplyName(userId, supplyName);
        item.setTotalUnitQuantity(quantity);
        return inventoryRepository.save(item);
    }

    // Update the quantity of the TOTAL SINGLE QUANTITY
    public Inventory updateSingleItemQuantity(Long userId, String supplyName, Double newQuantity) {
        Inventory item = getInventoryItemByUserAndSupplyName(userId, supplyName);
        // Update the total single quantity
        item.setTotalSingleQuantity(newQuantity);
        // Update the boxes as well
        double unitConversion = item.getUnitConversion();
        item.setTotalUnitQuantity(unitConversion > 0 ? newQuantity / unitConversion : 0);

        return inventoryRepository.save(item);
    }

    // Update the quantity of the TOTAL UNIT QUANTITY
    public Inventory updateUnitItemQuantity(Long userId, String supplyName, Double newUnitQuantity) {
        Inventory item = getInventoryItemByUserAndSupplyName(userId, supplyName);
        // Update the total amount of boxes
        item.setTotalUnitQuantity(newUnitQuantity);
        // Update the total amount of single diapers
        double unitConversion = item.getUnitConversion();
        item.setTotalSingleQuantity(item.getUnitConversion() > 0 ? newUnitQuantity * unitConversion: 0);

        return inventoryRepository.save(item);
    }
    // Helper Method
    private void recalcQuantities(Inventory inv) {
        double conv = inv.getUnitConversion();
        if (conv > 0) {
            inv.setTotalUnitQuantity(inv.getTotalSingleQuantity() / conv);
        } else {
            inv.setTotalUnitQuantity(0.0);
        }
    }

    // Update overall inventory item
    public Inventory updateInventory(Long inventoryId, Inventory details) {
        Inventory existing = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Inventory not found with ID: " + inventoryId));

        existing.setSupplyName(details.getSupplyName());
        existing.setPreferredSupplyMin(details.getPreferredSupplyMin());

        // Update quantities
        existing.setTotalSingleQuantity(details.getTotalSingleQuantity());
        existing.setUnitConversion(details.getUnitConversion());

        // Recalculate unit quantity based on updated fields
        recalcQuantities(existing);

        // Update user if changed
        if (details.getUser() != null) {
            User user = userRepository.findById(details.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + details.getUser().getId()));
            existing.setUser(user);
        }

        return inventoryRepository.save(existing);
    }

    // ---------------- DELETE ----------------
    public boolean deleteInventory(Long id) {
        if (inventoryRepository.existsById(id)) {
            inventoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
