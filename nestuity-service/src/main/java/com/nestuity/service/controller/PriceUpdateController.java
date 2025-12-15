package com.nestuity.service.controller;

import com.nestuity.service.dto.PriceUpdateRequest;
import com.nestuity.service.dto.PriceUpdateResponse;
import com.nestuity.service.service.PriceUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling price updates from the scraper service.
 */
@RestController
@RequestMapping("/api/price-updates")
public class PriceUpdateController {
    private static final Logger log = LoggerFactory.getLogger(PriceUpdateController.class);

    private final PriceUpdateService priceUpdateService;

    public PriceUpdateController(PriceUpdateService priceUpdateService) {
        this.priceUpdateService = priceUpdateService;
    }

    /**
     * Endpoint to receive price updates from the scraper.
     * Creates a new product if the URL doesn't exist, or adds a price history entry if it does.
     *
     * @param request Price update request from scraper
     * @return Response with success status and details
     */
    @PostMapping
    public ResponseEntity<PriceUpdateResponse> updatePrice(@RequestBody PriceUpdateRequest request) {
        log.info("Received price update request for URL: {}", request.getProductUrl());

        PriceUpdateResponse response = priceUpdateService.processPriceUpdate(request);

        if (response.isSuccess()) {
            // Return 201 for new products, 200 for updates
            HttpStatus status = response.isNewProduct() ? HttpStatus.CREATED : HttpStatus.OK;
            return ResponseEntity.status(status).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Health check endpoint to verify the price update service is running.
     *
     * @return Simple status message
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Price update service is running");
    }
}
