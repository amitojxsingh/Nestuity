package com.nestuity.service.service;

import com.nestuity.service.dto.DiaperUsageResponse;
import com.nestuity.service.entity.Baby;
import com.nestuity.service.entity.Inventory;
import com.nestuity.service.entity.User;
import com.nestuity.service.exception.ResourceNotFoundException;
import com.nestuity.service.repository.BabyRepository;
import com.nestuity.service.repository.InventoryRepository;
import com.nestuity.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiaperUsageCalculatorService {

    private final BabyRepository babyRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public DiaperUsageCalculatorService(BabyRepository babyRepository,
                                        UserRepository userRepository,
                                        InventoryRepository inventoryRepository) {
        this.babyRepository = babyRepository;
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
    }

    // Calculate the baby's diaper usage
    public DiaperUsageResponse calculateUsage(Long babyId) {
        Baby baby = babyRepository.findById(babyId)
                .orElseThrow(() -> new ResourceNotFoundException("Baby not found with ID: " + babyId));

        if (baby.getWeight() == null || baby.getWeight() <= 0) {
            return DiaperUsageResponse.missingWeight("Please enter your baby's weight to calculate usage.");
        }

        User parent = baby.getUser();
        // Find parent's inventory list
        List<Inventory> inventoryList = inventoryRepository.findByUserId(parent.getId());
        // Find row for parent's diaper info
        Optional<Inventory> diaperInventoryOpt = inventoryRepository
                .findByUserIdAndSupplyNameIgnoreCase(parent.getId(), "diapers");
        // Find how much diapers the parent still has
        double remainingDiapers = diaperInventoryOpt
                .map(Inventory::getTotalSingleQuantity)  // get the quantity if present
                .orElse(0.0);                 // default to 0 if no entry exists


        int dailyUsage = baby.getDailyUsage();

        // Find how much diapers are in the box
        double diapersPerBox = diaperInventoryOpt
                .map(Inventory::getUnitConversion)
                .orElse(0.0);

        // Compute how many days left of supply the parent has: total # of diapers / # of diapers used daily
        int daysLeft = dailyUsage > 0 && remainingDiapers > 0
                ? (int) Math.floor(remainingDiapers / dailyUsage)
                : 0;

        // Compute how many boxes the parent should buy so that they have enough:
        // Grab user-set amount of days minimum # of days worth of supply
        // minSupply (# of min days of supply user would want - daysLeft = # of days of supply required to buy
        double minSupply = diaperInventoryOpt
                .map(Inventory::getPreferredSupplyMin)  // get the quantity if present
                .orElse(0);                 // default to 0 if no entry exists


        // # of days of supply required to buy * # of diapers used per day = total # of diapers required to buy
        // total # of diapers required to buy / diapers per box = # of diaper boxes required to buy
        int recommendedPurchase = minSupply > 0
                ? (int) Math.ceil(((minSupply - daysLeft) * dailyUsage) / diapersPerBox)
                : 0;

        if (recommendedPurchase < 0) recommendedPurchase = 0;

        String message = recommendedPurchase == 0 ? "You have enough diapers for at least " + daysLeft + " days!" : null;

        return DiaperUsageResponse.builder()
                .remainingSupply(remainingDiapers)
                .daysLeft(daysLeft)
                .recommendedPurchase(recommendedPurchase)
                .message(message)
                .build();
    }

    // Method to update Diaper Usages
    public DiaperUsageResponse updateUsage(Long babyId, int diapersUsed) {
        if (diapersUsed < 0) {
            throw new IllegalArgumentException("Diapers used cannot be negative");
        }

        Baby baby = babyRepository.findById(babyId)
                .orElseThrow(() -> new ResourceNotFoundException("Baby not found with ID: " + babyId));

        User parent = baby.getUser();
        // Find parent's inventory list
        List<Inventory> inventoryList = inventoryRepository.findByUserId(parent.getId());
        // Find row for parent's diaper info
        Optional<Inventory> diaperInventoryOpt = inventoryRepository
                .findByUserIdAndSupplyNameIgnoreCase(parent.getId(), "diapers");
        // Find how much diapers the parent still has
        double remainingDiapers = diaperInventoryOpt
                .map(Inventory::getTotalSingleQuantity)  // get the quantity if present
                .orElse(0.0);                 // default to 0 if no entry exists

        double newSingleQuantity = remainingDiapers - diapersUsed;
        //  Update the total single quantity
        diaperInventoryOpt.ifPresent(diaperInventory -> {
            diaperInventory.setTotalSingleQuantity(newSingleQuantity);
            inventoryRepository.save(diaperInventory);
        });

        double diapersPerBox = diaperInventoryOpt
                .map(Inventory::getUnitConversion)
                .orElse(0.0);

        // Update the total unit quantity (how many boxes there are)
        double newBoxQuantity = newSingleQuantity / diapersPerBox;
        diaperInventoryOpt.ifPresent(diaperInventory -> {
            diaperInventory.setTotalUnitQuantity(newBoxQuantity);
            inventoryRepository.save(diaperInventory);
        });

        // Save updated remaining diapers
        userRepository.save(parent);

        // Return updated usage
        return calculateUsage(babyId);
    }

    // Method to update remaining diapers
    public void updateRemainingDiapers(Long userId, Double newQuantity) {
        User parent = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        List<Inventory> inventoryList = inventoryRepository.findByUserId(parent.getId());
        // Find row for parent's diaper info
        Optional<Inventory> diaperInventoryOpt = inventoryRepository
                .findByUserIdAndSupplyNameIgnoreCase(parent.getId(), "diapers");

        //  Update the total single quantity
        diaperInventoryOpt.ifPresent(diaperInventory -> {
            diaperInventory.setTotalSingleQuantity(newQuantity);
            inventoryRepository.save(diaperInventory);
        });

        double diapersPerBox = diaperInventoryOpt
                .map(Inventory::getUnitConversion)
                .orElse(0.0);

        // Update the total unit quantity (how many boxes there are)
        double newBoxQuantity = newQuantity / diapersPerBox;
        diaperInventoryOpt.ifPresent(diaperInventory -> {
            diaperInventory.setTotalUnitQuantity(newBoxQuantity);
            inventoryRepository.save(diaperInventory);
        });

        userRepository.save(parent);
    }

}
