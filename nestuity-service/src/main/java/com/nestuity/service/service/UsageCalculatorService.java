package com.nestuity.service.service;

import com.nestuity.service.dto.UsageCalculatorRequest;
import com.nestuity.service.entity.Baby;
import com.nestuity.service.entity.Inventory;
import com.nestuity.service.entity.User;
import com.nestuity.service.repository.BabyRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service for managing usage calculator operations.
 */
@Service
public class UsageCalculatorService {
    private static final String DIAPERS_SUPPLY_NAME = "Diapers";
    private static final int DEFAULT_PREFERRED_SUPPLY_MIN = 14;

    private final BabyRepository babyRepository;
    private final InventoryService inventoryService;

    public UsageCalculatorService(final BabyRepository babyRepository, final InventoryService inventoryService) {
        this.babyRepository = babyRepository;
        this.inventoryService = inventoryService;
    }

    /**
     * Updates baby information and diaper inventory
     *
     * @param request the usage calculator request
     * @throws ResponseStatusException if baby info not found
     */
    @Transactional
    public void updateUsageCalculator(final UsageCalculatorRequest request) {
        final Baby baby = babyRepository.findById(request.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Baby not found"));

        updateBabyInformation(baby, request);
        updateDiaperInventory(baby.getUser(), request);
    }

    /**
     * Updates baby entity with information from the request.
     */
    private void updateBabyInformation(final Baby baby, final UsageCalculatorRequest request) {
        baby.setDob(request.dob());
        baby.setDailyUsage(request.dailyUsage());
        baby.setWeight(request.weight());
        baby.setDiaperSize(request.diaperSize());
        babyRepository.save(baby);
    }

    /**
     * Updates or creates diaper inventory
     */
    private void updateDiaperInventory(final User user, final UsageCalculatorRequest request) {
        if (request.diapersPerBox() == null && request.boxesAtHome() == null) {
            return; // No inventory updates are needed
        }

        final Long userId = user.getId();
        final Inventory diaperInventory = getOrCreateDiaperInventory(user, userId, request);

        updateInventoryValues(diaperInventory, request);
        inventoryService.saveInventory(diaperInventory);
    }

    /**
     * Gets existing diaper inventory or creates a new one if it doesn't exist.
     */
    private Inventory getOrCreateDiaperInventory(final User user, final Long userId, final UsageCalculatorRequest request) {
        try {
            return inventoryService.getInventoryItemByUserAndSupplyName(userId, DIAPERS_SUPPLY_NAME);
        } catch (RuntimeException e) {
            return createNewDiaperInventory(user, request);
        }
    }

    /**
     * Creates a new diaper inventory entry.
     */
    private Inventory createNewDiaperInventory(final User user, final UsageCalculatorRequest request) {
        final Inventory inventory = new Inventory();
        inventory.setUser(user);
        inventory.setSupplyName(DIAPERS_SUPPLY_NAME);
        inventory.setPreferredSupplyMin(DEFAULT_PREFERRED_SUPPLY_MIN);
        inventory.setTotalSingleQuantity(0.0);
        inventory.setTotalUnitQuantity(0.0);
        inventory.setUnitConversion(request.diapersPerBox() != null ? request.diapersPerBox() : 0.0);
        return inventory;
    }

    /**
     * Updates inventory values based on the request data.
     */
    private void updateInventoryValues(final Inventory inventory, final UsageCalculatorRequest request) {
        if (request.diapersPerBox() != null) {
            inventory.setUnitConversion(request.diapersPerBox());
        }

        if (request.boxesAtHome() != null) {
            inventory.setTotalUnitQuantity(request.boxesAtHome());
            final double totalDiapers = request.boxesAtHome() * inventory.getUnitConversion();
            inventory.setTotalSingleQuantity(totalDiapers);
        }
    }
}
