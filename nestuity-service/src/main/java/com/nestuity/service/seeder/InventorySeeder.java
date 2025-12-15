package com.nestuity.service.seeder;

import com.nestuity.service.entity.Inventory;
import com.nestuity.service.entity.User;
import com.nestuity.service.repository.InventoryRepository;
import com.nestuity.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("dev")
public class InventorySeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InventorySeeder.class);

    private final UserService userService;
    private final InventoryRepository inventoryRepository;

    public InventorySeeder(UserService userService, InventoryRepository inventoryRepository) {
        this.userService = userService;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void run(String... args) {
        Optional<User> userOpt = userService.getUserByEmail("napoleon@test.com");
        if (userOpt.isEmpty()) {
            log.warn("⚠️ User not found, skipping inventory seeding");
            return;
        }

        User user = userOpt.get();

        if (inventoryRepository.findByUserId(user.getId()).isEmpty()) {
            Inventory inventory = new Inventory();
            inventory.setUser(user);
            inventory.setSupplyName("diapers");
            inventory.setTotalSingleQuantity(50.0);
            inventory.setTotalUnitQuantity(5.0);
            inventory.setUnitConversion(10.0);
            inventory.setPreferredSupplyMin(14); // default value

            inventoryRepository.save(inventory);
            log.info("🍼 Inventory seeded for user {}: {}", user.getEmail(), inventory.getSupplyName());
        } else {
            log.info("ℹ️ Inventory already exists for user {}", user.getEmail());
        }
    }
}
