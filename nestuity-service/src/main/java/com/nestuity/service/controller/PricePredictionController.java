package com.nestuity.service.controller;

import com.nestuity.service.entity.BabyProduct;
import com.nestuity.service.entity.PricePrediction;
import com.nestuity.service.service.PricePredictionService;
import com.nestuity.service.service.BabyProductService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/price-predictions")
public class PricePredictionController {
    private final PricePredictionService pricePredictionService;
    private final BabyProductService babyProductService;

    public PricePredictionController(PricePredictionService pricePredictionService, BabyProductService babyProductService) {
        this.pricePredictionService = pricePredictionService;
        this.babyProductService = babyProductService;
    }

    // Price Predictions CRUD -----------------------------------------------------
    // Create a new prediction
    @PostMapping
    public ResponseEntity<PricePrediction> createPrediction(@RequestBody PricePrediction prediction) {
        PricePrediction saved = pricePredictionService.savePrediction(prediction);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    // Get a prediction by ID
    @GetMapping("/{id}")
    public ResponseEntity<PricePrediction> getPredictionById(@PathVariable Long id) {
        Optional<PricePrediction> prediction = pricePredictionService.getPredictionById(id);
        return prediction.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // Get the most recent prediction for a baby product
    @GetMapping("/latest")
    public ResponseEntity<PricePrediction> getLatestForProduct(@RequestParam Long babyProductId) {
        // get baby product from babyproductservice
        BabyProduct babyProduct = babyProductService.findEntityById(babyProductId);
        Optional<PricePrediction> prediction = pricePredictionService.getLatestPrediction(babyProduct);
        return prediction.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // Get all predictions by trend
    @GetMapping("/trend/{trend}")
    public List<PricePrediction> getByTrend(@PathVariable String trend) {
        return pricePredictionService.getPredictionsByTrend(trend);
    }
    // Get all predictions above a confidence threshold
    @GetMapping("/confidence")
    public List<PricePrediction> getByConfidence(@RequestParam Float minConfidence) {
        return pricePredictionService.getPredictionsByConfidence(minConfidence);
    }
    // Get all predictions within a date range
    @GetMapping("/date-range")
    public List<PricePrediction> getByDateRange(@RequestParam ZonedDateTime start,
                                                @RequestParam ZonedDateTime end) {
        return pricePredictionService.getPredictionsBetweenDates(start, end);
    }
    // Update a prediction
    @PutMapping("/{id}")
    public ResponseEntity<PricePrediction> updatePrediction(@PathVariable Long id,
                                                            @RequestBody PricePrediction prediction) {
        Optional<PricePrediction> existing = pricePredictionService.getPredictionById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        prediction.setId(id);
        PricePrediction saved = pricePredictionService.savePrediction(prediction);
        return ResponseEntity.ok(saved);
    }
    // Delete a prediction
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrediction(@PathVariable Long id) {
        pricePredictionService.deletePredictionById(id);
        return ResponseEntity.noContent().build();
    }
}
