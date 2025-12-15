package com.nestuity.service.repository;

import com.nestuity.service.entity.PricePrediction;
import com.nestuity.service.entity.BabyProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.ZonedDateTime;

@Repository
public interface PricePredictionRepository extends JpaRepository<PricePrediction, Long> {
    // most recent prediction for a given baby product
    Optional<PricePrediction> findFirstByBabyProductOrderByPredictionDateDesc(BabyProduct babyProduct);

    // all predictions by trend
    List<PricePrediction> findByTrend(String trend);

    // all predictions over a certain confidence level
    List<PricePrediction> findByConfidenceGreaterThanEqual(Float confidence);

    // all predictions in a date range
    List<PricePrediction> findByPredictionDateBetween(ZonedDateTime startDate, ZonedDateTime endDate);
}