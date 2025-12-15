package com.nestuity.service.service;

import com.nestuity.service.dto.PriceUpdateRequest;
import com.nestuity.service.dto.PriceUpdateResponse;
import com.nestuity.service.entity.BabyProduct;
import com.nestuity.service.repository.BabyProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PriceUpdateService.
 */
class PriceUpdateServiceTest {

    private BabyProductRepository babyProductRepository;
    private PriceUpdateService priceUpdateService;

    @BeforeEach
    void setUp() {
        babyProductRepository = mock(BabyProductRepository.class);
        priceUpdateService = new PriceUpdateService(babyProductRepository);
    }

    @Test
    void processPriceUpdate_ShouldCreateNewProduct_WhenNotExists() {
        // Arrange
        PriceUpdateRequest request = new PriceUpdateRequest();
        request.setProductUrl("http://example.com/product1");
        request.setProductName("Baby Bottle");
        request.setRetailer("Walmart");
        request.setPrice(BigDecimal.valueOf(19.99));
        request.setCategory("Feeding");
        request.setBrand("Philips");

        when(babyProductRepository.findByPriceHistoryProductUrl(request.getProductUrl()))
                .thenReturn(Optional.empty());

        BabyProduct saved = new BabyProduct();
        saved.setId(1L);
        saved.setName("Baby Bottle");

        when(babyProductRepository.save(any(BabyProduct.class))).thenReturn(saved);

        // Act
        PriceUpdateResponse response = priceUpdateService.processPriceUpdate(request);

        // Assert
        assertTrue(response.isSuccess());
        assertTrue(response.isNewProduct());
        assertEquals(1L, response.getProductId());
        assertTrue(response.getMessage().toLowerCase().contains("baby bottle"));
        verify(babyProductRepository, times(1)).save(any(BabyProduct.class));
    }

    @Test
    void processPriceUpdate_ShouldUpdateExistingProduct_WhenExists() {
        // Arrange
        PriceUpdateRequest request = new PriceUpdateRequest();
        request.setProductUrl("http://example.com/product2");
        request.setProductName("Baby Wipes");
        request.setRetailer("Amazon");
        request.setPrice(BigDecimal.valueOf(9.49));

        BabyProduct existing = new BabyProduct();
        existing.setId(2L);
        existing.setName("Baby Wipes");

        when(babyProductRepository.findByPriceHistoryProductUrl(request.getProductUrl()))
                .thenReturn(Optional.of(existing));

        when(babyProductRepository.save(any(BabyProduct.class))).thenReturn(existing);

        // Act
        PriceUpdateResponse response = priceUpdateService.processPriceUpdate(request);

        // Assert
        assertTrue(response.isSuccess());
        assertFalse(response.isNewProduct());
        assertEquals(2L, response.getProductId());
        verify(babyProductRepository, times(1)).save(any(BabyProduct.class));
    }

    @Test
    void processPriceUpdate_ShouldReturnError_WhenInvalidRequest() {
        // Arrange
        PriceUpdateRequest invalid = new PriceUpdateRequest();
        invalid.setProductUrl(null); // invalid

        // Act
        PriceUpdateResponse response = priceUpdateService.processPriceUpdate(invalid);

        // Assert
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Invalid request"));
        verify(babyProductRepository, never()).save(any());
    }

    @Test
    void processPriceUpdate_ShouldHandleRepositoryExceptionGracefully() {
        // Arrange
        PriceUpdateRequest request = new PriceUpdateRequest();
        request.setProductUrl("http://example.com/error");
        request.setProductName("Baby Lotion");
        request.setRetailer("Target");
        request.setPrice(BigDecimal.valueOf(15.00));

        when(babyProductRepository.findByPriceHistoryProductUrl(request.getProductUrl()))
                .thenThrow(new RuntimeException("DB error"));

        // Act
        PriceUpdateResponse response = priceUpdateService.processPriceUpdate(request);

        // Assert
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Failed to process price update"));
    }
}
