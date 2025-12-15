package com.nestuity.service.repository;

import com.nestuity.service.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    List<PriceHistory> findByBabyProductIdOrderByDateAsc(Long productId);
}

