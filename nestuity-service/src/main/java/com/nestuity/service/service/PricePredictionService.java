package com.nestuity.service.service;

import com.nestuity.service.entity.BabyProduct;
import com.nestuity.service.entity.PricePrediction;
import com.nestuity.service.repository.PricePredictionRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PricePredictionService {
    private final PricePredictionRepository pricePredictionRepository;

    public PricePredictionService(PricePredictionRepository pricePredictionRepository) {
        this.pricePredictionRepository = pricePredictionRepository;
    }

    // Save a new price prediction
    public PricePrediction savePrediction(PricePrediction prediction) {
        return pricePredictionRepository.save(prediction);
    }

    // Get a prediction by ID
    public Optional<PricePrediction> getPredictionById(Long id) {
        return pricePredictionRepository.findById(id);
    }

    // Get the latest prediction for a specific baby product
    public Optional<PricePrediction> getLatestPrediction(BabyProduct product) {
        return pricePredictionRepository.findFirstByBabyProductOrderByPredictionDateDesc(product);
    }

    // Get all predictions by trend
    public List<PricePrediction> getPredictionsByTrend(String trend) {
        return pricePredictionRepository.findByTrend(trend);
    }

    // Get all predictions above a confidence threshold
    public List<PricePrediction> getPredictionsByConfidence(Float minConfidence) {
        return pricePredictionRepository.findByConfidenceGreaterThanEqual(minConfidence);
    }

    // Get all predictions in a date range
    public List<PricePrediction> getPredictionsBetweenDates(ZonedDateTime start, ZonedDateTime end) {
        return pricePredictionRepository.findByPredictionDateBetween(start, end);
    }

    // delete a prediction by ID
    public void deletePredictionById(Long id) {
        pricePredictionRepository.deleteById(id);
    }
}