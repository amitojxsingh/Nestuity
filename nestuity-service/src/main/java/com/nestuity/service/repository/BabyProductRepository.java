package com.nestuity.service.repository;

import com.nestuity.service.entity.BabyProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BabyProductRepository extends JpaRepository<BabyProduct, Long> {

    /**
     * Find a BabyProduct by a product URL in its price history.
     * This allows us to update existing products when scraping by URL.
     *
     * @param productUrl The product URL to search for
     * @return Optional containing the BabyProduct if found
     */
    @Query("SELECT DISTINCT bp FROM BabyProduct bp JOIN bp.priceHistory ph WHERE ph.productUrl = :productUrl")
    Optional<BabyProduct> findByPriceHistoryProductUrl(@Param("productUrl") String productUrl);
}
