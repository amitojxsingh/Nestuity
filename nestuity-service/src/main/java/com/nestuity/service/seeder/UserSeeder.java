package com.nestuity.service.seeder;

import com.nestuity.service.entity.Baby;
import com.nestuity.service.entity.User;
import com.nestuity.service.service.UserService;
import com.nestuity.service.repository.BabyRepository;
import com.nestuity.service.service.BabyReminderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

/**
 * Seeds the database with a single user for testing purposes.
 * Only runs when 'dev' profile is active.
 */
@Component
@Profile("dev")
public class UserSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UserSeeder.class);
    private final UserService userService;
    private final BabyRepository babyRepository;
    private final BabyReminderService reminderService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserSeeder(UserService userService, BabyRepository babyRepository, BabyReminderService reminderService) {
        this.userService = userService;
        this.babyRepository = babyRepository;
        this.reminderService = reminderService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public void run(String... args) {
        String testEmail = "napoleon@test.com";

        Optional<User> existing = userService.getUserByEmail(testEmail);
        if (existing.isPresent()) {
            log.info("👤 Test user already exists: {}", testEmail);
            return;
        }

        User user = new User();
        user.setEmail(testEmail);
        user.setFirstName("Napoleon");
        user.setLastName("Bonaparte");
        user.setPasswordHash(passwordEncoder.encode("Napoleon@1")); // simple password for Cypress
        user.setAuthProvider("credentials");
        user.setActive(true);
        user.setPreferences(null); // if you have a default prefs method

        User created = userService.saveUser(user);
        log.info("🌱 Test user created: {} {} (email: {})", created.getFirstName(), created.getLastName(), created.getEmail());

        // --- create a default baby for this user ---
        if (babyRepository.findByUserId(created.getId()).isEmpty()) {
            Baby baby = new Baby();
            baby.setUser(created);
            baby.setName("Andrea");
            baby.setDob(new Date(125, 3, 1)); // 2025-04-01 (year offset from 1900)
            baby.setWeight(3.2);
            baby.setDiaperSize("2");

            babyRepository.save(baby);
            log.info("👶 Baby created: {} for user {}", baby.getName(), created.getEmail());

            reminderService.createReminder(baby.getId());
            log.info("⏰ Reminders created for baby ID {}", baby.getId());
        }
    }
}
