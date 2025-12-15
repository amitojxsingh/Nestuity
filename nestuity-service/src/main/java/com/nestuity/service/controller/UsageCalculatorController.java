package com.nestuity.service.controller;

import com.nestuity.service.dto.UsageCalculatorRequest;
import com.nestuity.service.service.UsageCalculatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling usage calculator updates.
 */
@RestController
@RequestMapping("/api/usage-calculator")
public class UsageCalculatorController {
    private final UsageCalculatorService usageCalculatorService;

    public UsageCalculatorController(final UsageCalculatorService usageCalculatorService) {
        this.usageCalculatorService = usageCalculatorService;
    }

    /**
     * Updates baby information and diaper inventory.
     *
     * @param request the usage calculator data
     * @return success response
     */
    @PostMapping("/edit")
    public ResponseEntity<String> updateUsageCalculator(@Validated @RequestBody final UsageCalculatorRequest request) {
        usageCalculatorService.updateUsageCalculator(request);
        return ResponseEntity.ok("Usage calculator updated successfully");
    }
}
