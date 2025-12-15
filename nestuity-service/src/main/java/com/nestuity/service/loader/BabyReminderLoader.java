package com.nestuity.service.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nestuity.service.entity.BabyReminder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class BabyReminderLoader {

    private final ObjectMapper objectMapper;

    public BabyReminderLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<BabyReminder> loadReminders() {
        try {
            ClassPathResource resource = new ClassPathResource("baby_reminders.json");

            if (!resource.exists()) {
                throw new RuntimeException("baby_reminders.json NOT found on classpath!");
            }

            try (InputStream inputStream = resource.getInputStream()) {
                return Arrays.asList(objectMapper.readValue(inputStream, BabyReminder[].class));
            }
        } catch (Exception e) {
            log.error("❌ Failed to load baby reminders JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load baby reminders JSON", e);
        }
    }
}
