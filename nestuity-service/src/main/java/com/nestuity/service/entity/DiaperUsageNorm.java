package com.nestuity.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaperUsageNorm {
    private Double minWeight;
    private Double maxWeight;
    private Integer diapersPerDay;

    // Utility method to check if weight falls in this range
    public boolean matches(Double weight) {
        return weight >= minWeight && weight <= maxWeight;
    }
}
