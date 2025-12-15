package com.nestuity.service.dto;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class DiaperUsageResponse {

    private Double remainingSupply;
    private int daysLeft;
    private int recommendedPurchase;
    private String message;

    public static DiaperUsageResponse missingWeight(String message) {
        return DiaperUsageResponse.builder().message(message).build();
    }
}



