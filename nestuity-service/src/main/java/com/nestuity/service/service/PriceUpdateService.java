package com.nestuity.service.service;

import com.nestuity.service.dto.PriceUpdateRequest;
import com.nestuity.service.dto.PriceUpdateResponse;
import com.nestuity.service.entity.BabyProduct;
import com.nestuity.service.entity.PriceHistory;
import com.nestuity.service.repository.BabyProductRepository;
import com.nestuity.service.repository.PriceHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Service
public class PriceUpdateService {
    private static final Logger log = LoggerFactory.getLogger(PriceUpdateService.class);

    private final BabyProductRepository babyProductRepository;

    public PriceUpdateService(BabyProductRepository babyProductRepository) {
        this.babyProductRepository = babyProductRepository;
    }

    /**
     * Process a price update from the scraper.
     * If a product with this URL exists, add a new price history entry.
     * If not, create a new product.
     *
     * @param request The price update request from the scraper
     * @return Response indicating success/failure and whether a new product was created
     */
    @Transactional
    public PriceUpdateResponse processPriceUpdate(PriceUpdateRequest request) {
        log.info("Processing price update for URL: {}", request.getProductUrl());

        // Validate request
        if (!isValidRequest(request)) {
            return PriceUpdateResponse.error("Invalid request: missing required fields");
        }

        try {
            // Check if product already exists by URL
            Optional<BabyProduct> existingProduct = babyProductRepository.findByPriceHistoryProductUrl(request.getProductUrl());

            BabyProduct product;
            boolean isNewProduct;

            if (existingProduct.isPresent()) {
                // Update existing product
                product = existingProduct.get();
                isNewProduct = false;
                log.info("Found existing product: {} (ID: {})", product.getName(), product.getId());

                // Optionally update product details if provided
                updateProductDetails(product, request);
            } else {
                // Create new product
                product = createNewProduct(request);
                isNewProduct = true;
                log.info("Creating new product: {}", product.getName());
            }

            // Add new price history entry
            PriceHistory priceHistory = createPriceHistory(request);
            product.addPriceHistory(priceHistory);

            // Save product (cascades to price history)
            BabyProduct savedProduct = babyProductRepository.save(product);

            log.info("Successfully {} product: {} (ID: {})",
                    isNewProduct ? "created" : "updated",
                    savedProduct.getName(),
                    savedProduct.getId());

            return PriceUpdateResponse.success(
                    savedProduct.getId(),
                    isNewProduct,
                    savedProduct.getName()
            );

        } catch (Exception e) {
            log.error("Error processing price update: {}", e.getMessage(), e);
            return PriceUpdateResponse.error("Failed to process price update: " + e.getMessage());
        }
    }

    private boolean isValidRequest(PriceUpdateRequest request) {
        return request != null
                && request.getProductUrl() != null && !request.getProductUrl().trim().isEmpty()
                && request.getRetailer() != null && !request.getRetailer().trim().isEmpty()
                && request.getPrice() != null
                && request.getProductName() != null && !request.getProductName().trim().isEmpty();
    }

    private BabyProduct createNewProduct(PriceUpdateRequest request) {
        BabyProduct product = new BabyProduct();
        product.setName(request.getProductName());
        product.setBrand(request.getBrand());
        product.setCategory(request.getCategory());
        product.setDescription(request.getDescription());
        product.setCurrency(request.getCurrency() != null ? request.getCurrency() : "CAD");
        product.setInStock(request.getInStock() != null ? request.getInStock() : true);
        return product;
    }

    private void updateProductDetails(BabyProduct product, PriceUpdateRequest request) {
        // Update product details if they were provided and are different
        if (request.getBrand() != null && !request.getBrand().isEmpty()) {
            product.setBrand(request.getBrand());
        }
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            product.setCategory(request.getCategory());
        }
        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            product.setDescription(request.getDescription());
        }
        if (request.getInStock() != null) {
            product.setInStock(request.getInStock());
        }
    }

    private PriceHistory createPriceHistory(PriceUpdateRequest request) {
        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setRetailer(request.getRetailer());
        priceHistory.setProductUrl(request.getProductUrl());
        priceHistory.setPrice(request.getPrice());

        // Parse scraped timestamp or use current time
        LocalDateTime timestamp = parseTimestamp(request.getScrapedAt());
        priceHistory.setDate(timestamp);

        return priceHistory;
    }

    private LocalDateTime parseTimestamp(String scrapedAt) {
        if (scrapedAt == null || scrapedAt.isEmpty()) {
            return LocalDateTime.now();
        }

        try {
            return LocalDateTime.parse(scrapedAt);
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse timestamp '{}', using current time", scrapedAt);
            return LocalDateTime.now();
        }
    }
}
