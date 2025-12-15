package com.nestuity.service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.ZonedDateTime;

import java.util.ArrayList;

import com.nestuity.service.entity.BabyProduct;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricePrediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "baby_product_id", nullable = false)
    private BabyProduct babyProduct;

    private Float predictedPrice;  // TODO: to start let's do linear regression?
    private ZonedDateTime predictionDate;
    private Float confidence;
    private String trend;  // ["increasing", "decreasing", "stable"]
    private ArrayList<String> factors;
//    private String modelVersion;  // e.g., "linear_regression_v1"
//    private String currency;

    private ZonedDateTime createdAt;
}