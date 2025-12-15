package com.nestuity.service.controller;

import com.nestuity.service.dto.BabyResponse;
import com.nestuity.service.dto.DiaperUsageResponse;
import com.nestuity.service.entity.Baby;
import com.nestuity.service.entity.User;
import com.nestuity.service.entity.DiaperUsageNorm;
import com.nestuity.service.exception.ResourceNotFoundException;
import com.nestuity.service.repository.BabyRepository;
import com.nestuity.service.repository.UserRepository;
import com.nestuity.service.service.BabyReminderService;
import com.nestuity.service.service.DiaperUsageCalculatorService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/babies")
public class BabyController {

    private final BabyRepository babyRepository;
    private final UserRepository userRepository;
    private final DiaperUsageCalculatorService diaperUsageCalculatorService;
    private final BabyReminderService babyReminderService;

    private static final List<DiaperUsageNorm> USAGE_NORMS = List.of(
            // TODO: Make these calculations instead of hardcoded tables for the weights
        new DiaperUsageNorm(1.5, 2.4, 12),
        new DiaperUsageNorm(2.5, 4.5, 10),
        new DiaperUsageNorm(4.6, 6.0, 8),
        new DiaperUsageNorm(6.1, 9.0, 6),
        new DiaperUsageNorm(9.1, 12.0, 5),
        new DiaperUsageNorm(12.1, 14.0, 3)
    );

    private static final Map<String, Integer> diapersPerBox = Map.of(
        "0", 140,
        "1", 164,
        "2", 142,
        "3", 136,
        "4", 23
    );
    
    @Autowired
    public BabyController(BabyRepository babyRepository, UserRepository userRepository, DiaperUsageCalculatorService diaperUsageCalculatorService, BabyReminderService babyReminderService) {
        this.babyRepository = babyRepository;
        this.userRepository = userRepository;
        this.diaperUsageCalculatorService = diaperUsageCalculatorService;
        this.babyReminderService = babyReminderService;
    }

    // ------------------- CREATE -------------------
    @Transactional
    @PostMapping
    public ResponseEntity<BabyResponse> createBaby(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        User parent = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Baby baby = new Baby();
        baby.setName(body.get("name").toString());
        baby.setUser(parent);
        Double weight = Double.valueOf(body.get("weight").toString());
        baby.setWeight(weight);
        if (weight == null || weight <= 0) {
            throw new IllegalArgumentException("Please enter your baby's weight to calculate usage.");
        }
        DiaperUsageNorm norm = USAGE_NORMS.stream()
                .filter(n -> n.matches(weight))
                .findFirst()
                .orElse(null);

        int dailyUsage = norm != null ? Math.round(norm.getDiapersPerDay()) : 3;
        baby.setDailyUsage(dailyUsage);

        try {
            baby.setDob(java.sql.Date.valueOf(body.get("dob").toString()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format, expected YYYY-MM-DD");
        }

        Object diaperSizeObj = body.get("diaperSize");
        String diaperSize = (diaperSizeObj != null && !diaperSizeObj.toString().isBlank())
                ? diaperSizeObj.toString()
                : "1";
        baby.setDiaperSize(diaperSize);
        Integer perBox = diapersPerBox.get(diaperSize);

        Baby saved = babyRepository.save(baby);
        babyReminderService.createReminder(saved.getId());
        return ResponseEntity.ok(new BabyResponse(saved));
    }

    // ------------------- READ ALL -------------------
    @GetMapping
    public ResponseEntity<List<BabyResponse>> getAllBabies() {
        List<BabyResponse> dtos = babyRepository.findAll()
                .stream()
                .map(BabyResponse::new)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // ------------------- READ BY USER -------------------
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BabyResponse>> getBabiesByUserId(@PathVariable Long userId) {
        List<BabyResponse> babies = babyRepository.findAll().stream()
                .filter(b -> b.getUser().getId().equals(userId))
                .map(BabyResponse::new)
                .toList();

        return ResponseEntity.ok(babies);
    }

    // ------------------- READ BY BABY ID -------------------
    @GetMapping("/{id}")
    public ResponseEntity<BabyResponse> getBabyById(@PathVariable Long id) {
        return babyRepository.findById(id)
                .map(BabyResponse::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------- UPDATE -------------------
    @PutMapping("/{id}")
    public ResponseEntity<BabyResponse> updateBaby(@PathVariable Long id, @RequestBody Baby updatedBaby) {
        Integer newDiapersPerBox = diapersPerBox.get(updatedBaby.getDiaperSize());
        // set new daily usage if user manually entered
        // else daily usage needs to be updated according to updatedBaby new statistics
        Integer newDailyUsage;
        if (updatedBaby.getDailyUsage() < 0) {
            DiaperUsageNorm norm = USAGE_NORMS.stream()
                .filter(n -> n.matches(updatedBaby.getWeight()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No diaper usage norms found for baby weight: " + updatedBaby.getWeight()
                ));
            newDailyUsage = Math.round(norm.getDiapersPerDay());
        } else {
            newDailyUsage = updatedBaby.getDailyUsage();
        }
        
        return babyRepository.findById(id)
                .map(existingBaby -> {
                    existingBaby.setName(updatedBaby.getName());
                    existingBaby.setDob(updatedBaby.getDob());
                    existingBaby.setWeight(updatedBaby.getWeight());
                    existingBaby.setDiaperSize(updatedBaby.getDiaperSize());
                    existingBaby.setDailyUsage(newDailyUsage);
                    Baby savedBaby = babyRepository.save(existingBaby);
                    return ResponseEntity.ok(new BabyResponse(savedBaby));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------- DELETE -------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBaby(@PathVariable Long id) {
        return babyRepository.findById(id)
                .map(baby -> {
                    babyRepository.delete(baby);
                    return ResponseEntity.ok("Baby deleted successfully.");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------- READ BABY'S REMAINING DIAPER STATS  -------------------
    @GetMapping("/{babyId}/diaper-usage")
    public ResponseEntity<DiaperUsageResponse> getDiaperUsage(@PathVariable Long babyId) {
        DiaperUsageResponse usage = diaperUsageCalculatorService.calculateUsage(babyId);
        return ResponseEntity.ok(usage);
    }

    // ------------------- UPDATE BABY'S REMAINING DIAPER STATS -------------------

    @PutMapping("/{babyId}/diaper-usage")
    public ResponseEntity<DiaperUsageResponse> updateDiaperUsage(
            @PathVariable Long babyId,
            @RequestBody Map<String, Object> body) {

        int diapersUsed = Integer.parseInt(body.get("diapersUsed").toString());
        DiaperUsageResponse updatedUsage = diaperUsageCalculatorService.updateUsage(babyId, diapersUsed);

        return ResponseEntity.ok(updatedUsage);
    }
}
