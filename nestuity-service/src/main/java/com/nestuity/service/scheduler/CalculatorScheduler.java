package com.nestuity.service.scheduler;

import com.nestuity.service.entity.Baby;
import com.nestuity.service.entity.Inventory;
import com.nestuity.service.entity.User;
import com.nestuity.service.service.*;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class CalculatorScheduler {

    private final BabyService babyService;
    private final EmailService emailService;
    private final DiaperUsageCalculatorService diaperUsageCalculatorService;
    private final InventoryService inventoryService;

    // Only send reminders when daysLeft matches one of these values
    private static final Set<Integer> REMINDER_DAYS = Set.of(7, 5, 3, 2, 1, 0);

    @Autowired
    public CalculatorScheduler(BabyService babyService,
                               EmailService emailService,
                               InventoryService inventoryService,
                               DiaperUsageCalculatorService diaperUsageCalculatorService) {
        this.babyService = babyService;
        this.emailService = emailService;
        this.diaperUsageCalculatorService = diaperUsageCalculatorService;
        this.inventoryService = inventoryService;
    }

    @Scheduled(cron = "0 0 0 * * *") // 00:00:00 everyday (midnight) 
    @Transactional
    public void runTask() {
        List<Baby> babies = babyService.getAllBabies();
        for (Baby baby: babies) {
            User parent = baby.getUser();
            // Grab baby's daily usage
            int dailyUsage = baby.getDailyUsage();
            // Grab diaper inventory for this user
            Inventory diaperInventory = inventoryService.getInventoryItemByUserAndSupplyName(parent.getId(), "diapers");
            // Update the total single quantity
            double remainingSingles = diaperInventory.getTotalSingleQuantity() - dailyUsage;
            remainingSingles = Math.max(0, remainingSingles); // prevent negative
            diaperInventory.setTotalSingleQuantity(remainingSingles);

            // Recalculate total boxes
            double unitConversion = diaperInventory.getUnitConversion(); // singles per box
            double remainingBoxes = unitConversion > 0 ? remainingSingles / unitConversion : 0;
            diaperInventory.setTotalUnitQuantity(remainingBoxes);

            // Save changes to inventory
            inventoryService.updateInventory(diaperInventory.getId(), diaperInventory);

            // Check if a reminder is needed
            var usageResponse = diaperUsageCalculatorService.calculateUsage(baby.getId());
            int daysLeft = usageResponse.getDaysLeft();
            // Skip if notifications OFF OR daysLeft not in reminder list
            if (parent.getPreferences() == null
                    || !parent.getPreferences().isEmailNotificationsEnabled()
                    || !REMINDER_DAYS.contains(daysLeft)) {
                continue;
            }
            try {
                String fullName = parent.getFirstName() + " " + parent.getLastName();
                emailService.sendDiaperReminderEmail(parent.getEmail(), fullName, daysLeft);
                System.out.println("Reminder sent to: " + parent.getEmail());
            } catch (IOException e) {
                // exception ignored
            }

        }
    }
}
