package com.nestuity.service.dto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Date;

/**
 * Data Transfer Object for usage calculator updates.
 * Contains baby information and diaper inventory data.
 */
public record UsageCalculatorRequest(
        @Nonnull
        Long id,

        @Nonnull
        Date dob,

        @Nonnull
        Double weight,

        @Nonnull
        String diaperSize,

        @Nullable
        Integer dailyUsage,

        @Nullable
        Double diapersPerBox,

        @Nullable
        Double boxesAtHome
) {
}