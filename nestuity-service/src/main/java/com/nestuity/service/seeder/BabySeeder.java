package com.nestuity.service.seeder;

import com.nestuity.service.entity.Baby;
import com.nestuity.service.entity.User;
import com.nestuity.service.repository.BabyRepository;
import com.nestuity.service.repository.UserRepository;
import com.nestuity.service.service.BabyReminderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Seeds the database with test baby and user data.
 * Only runs when 'dev' profile is active.
 */
@Component
@Profile("dev")
public class BabySeeder implements CommandLineRunner {

    private final BabyRepository babyRepository;
    private final UserRepository userRepository;
    private final BabyReminderService reminderService;

    public BabySeeder(BabyRepository bRepository, UserRepository uRepository, BabyReminderService reminderService) {
        this.babyRepository = bRepository;
        this.userRepository = uRepository;
        this.reminderService = reminderService;
    }

    @Override
    public void run(String... args) {
        if (babyRepository.count() == 0) {
            User user = new User();
            user.setFirstName("YingChien");
            user.setEmail("test@email.com");
            user.setPasswordHash("hash");
            user.setActive(true);
            user.setPhoneNumber("123-456-7890");
            user.setPreferences(null);

            userRepository.save(user);
            System.out.println("User seeding completed.");

            Baby baby = new Baby();
            baby.setUser(user);
            baby.setName("Andrea");
            baby.setDob(new Date(125, 3, 1)); // year offset from 1900: 2025-04-01
            baby.setWeight(3.2);
            baby.setDiaperSize("2");

            babyRepository.save(baby);
            System.out.println("Baby seeding completed.");

            reminderService.createReminder(baby.getId());
            System.out.println("Reminders created for baby ID " + baby.getId());
        }

        System.out.println("Baby and User seeding completed.");
    }
}