package com.nestuity.service.service;

import com.nestuity.service.dto.BabyProductResponse;
import com.nestuity.service.dto.CreateBabyProductRequest;
import com.nestuity.service.dto.PriceHistoryDTO;
import com.nestuity.service.dto.UpdateBabyProductRequest;
import com.nestuity.service.entity.BabyProduct;
import com.nestuity.service.exception.ResourceNotFoundException;
import com.nestuity.service.repository.BabyProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BabyProductServiceTest {

    @Mock
    private BabyProductRepository babyProductRepository;

    @InjectMocks
    private BabyProductService babyProductService;

    private BabyProduct testProduct;
    private CreateBabyProductRequest createRequest;
    private UpdateBabyProductRequest updateRequest;

    @BeforeEach
    void setUp() {
        // Setup test product
        testProduct = new BabyProduct();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setBrand("Test Brand");
        testProduct.setCategory("Test Category");
        testProduct.setDescription("Test Description");
        testProduct.setCurrency("CAD");
        testProduct.setInStock(true);
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());

        // Setup create request
        createRequest = new CreateBabyProductRequest();
        createRequest.setName("New Product");
        createRequest.setBrand("New Brand");
        createRequest.setCategory("Feeding");
        createRequest.setDescription("New Description");
        createRequest.setCurrency("CAD");
        createRequest.setInStock(true);

        // Setup update request
        updateRequest = new UpdateBabyProductRequest();
        updateRequest.setName("Updated Product");
        updateRequest.setBrand("Updated Brand");
    }

    // ==================== CREATE TESTS ====================

    @Test
    void createBabyProductWithValidDataSucceedsTest() {
        // Given
        when(babyProductRepository.save(any(BabyProduct.class))).thenReturn(testProduct);

        // When
        BabyProductResponse response = babyProductService.createBabyProduct(createRequest);

        // Then
        assertNotNull(response);
        assertEquals(testProduct.getId(), response.getId());
        assertEquals(testProduct.getName(), response.getName());
        verify(babyProductRepository, times(1)).save(any(BabyProduct.class));
    }

    @Test
    void createBabyProductWithPriceHistorySucceedsTest() {
        // Given
        PriceHistoryDTO priceDTO = new PriceHistoryDTO();
        priceDTO.setRetailer("Amazon");
        priceDTO.setPrice(new BigDecimal("29.99"));
        priceDTO.setDate(LocalDateTime.now());
        priceDTO.setProductUrl("https://amazon.ca/product/123");
        
        createRequest.setPriceHistory(Arrays.asList(priceDTO));
        when(babyProductRepository.save(any(BabyProduct.class))).thenReturn(testProduct);

        // When
        BabyProductResponse response = babyProductService.createBabyProduct(createRequest);

        // Then
        assertNotNull(response);
        verify(babyProductRepository, times(1)).save(any(BabyProduct.class));
    }

    @Test
    void createBabyProductWithoutNameThrowsExceptionTest() {
        // Given
        createRequest.setName(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> babyProductService.createBabyProduct(createRequest)
        );
        assertEquals("Product name is required", exception.getMessage());
        verify(babyProductRepository, never()).save(any(BabyProduct.class));
    }

    @Test
    void createBabyProductWithEmptyNameThrowsExceptionTest() {
        // Given
        createRequest.setName("   ");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> babyProductService.createBabyProduct(createRequest)
        );
        assertEquals("Product name is required", exception.getMessage());
    }

    @Test
    void createBabyProductWithTooLongNameThrowsExceptionTest() {
        // Given
        createRequest.setName("a".repeat(256));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> babyProductService.createBabyProduct(createRequest)
        );
        assertEquals("Product name must not exceed 255 characters", exception.getMessage());
    }

    @Test
    void createBabyProductSetsDefaultCurrencyTest() {
        // Given
        createRequest.setCurrency(null);
        when(babyProductRepository.save(any(BabyProduct.class))).thenReturn(testProduct);

        // When
        babyProductService.createBabyProduct(createRequest);

        // Then
        verify(babyProductRepository).save(argThat(product -> 
            "CAD".equals(product.getCurrency())
        ));
    }

    @Test
    void createBabyProductSetsDefaultInStockTest() {
        // Given
        createRequest.setInStock(null);
        when(babyProductRepository.save(any(BabyProduct.class))).thenReturn(testProduct);

        // When
        babyProductService.createBabyProduct(createRequest);

        // Then
        verify(babyProductRepository).save(argThat(product -> 
            Boolean.TRUE.equals(product.getInStock())
        ));
    }

    // ==================== READ TESTS ====================

    @Test
    void findByIdWithValidIdReturnsProductTest() {
        // Given
        when(babyProductRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        BabyProductResponse response = babyProductService.findById(1L);

        // Then
        assertNotNull(response);
        assertEquals(testProduct.getId(), response.getId());
        assertEquals(testProduct.getName(), response.getName());
        verify(babyProductRepository, times(1)).findById(1L);
    }

    @Test
    void findByIdWithInvalidIdThrowsExceptionTest() {
        // Given
        when(babyProductRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> babyProductService.findById(999L)
        );
        assertEquals("BabyProduct not found with id 999", exception.getMessage());
    }

    @Test
    void findAllReturnsAllProductsTest() {
        // Given
        BabyProduct product2 = new BabyProduct();
        product2.setId(2L);
        product2.setName("Product 2");
        
        when(babyProductRepository.findAll()).thenReturn(Arrays.asList(testProduct, product2));

        // When
        List<BabyProductResponse> responses = babyProductService.findAll();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(babyProductRepository, times(1)).findAll();
    }

    @Test
    void findAllReturnsEmptyListWhenNoProductsTest() {
        // Given
        when(babyProductRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<BabyProductResponse> responses = babyProductService.findAll();

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    // ==================== UPDATE TESTS ====================

    @Test
    void updateBabyProductWithValidDataSucceedsTest() {
        // Given
        when(babyProductRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(babyProductRepository.save(any(BabyProduct.class))).thenReturn(testProduct);

        // When
        BabyProductResponse response = babyProductService.updateBabyProduct(1L, updateRequest);

        // Then
        assertNotNull(response);
        verify(babyProductRepository, times(1)).findById(1L);
        verify(babyProductRepository, times(1)).save(any(BabyProduct.class));
    }

    @Test
    void updateBabyProductWithInvalidIdThrowsExceptionTest() {
        // Given
        when(babyProductRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> babyProductService.updateBabyProduct(999L, updateRequest)
        );
        assertEquals("BabyProduct not found with id 999", exception.getMessage());
        verify(babyProductRepository, never()).save(any(BabyProduct.class));
    }

    @Test
    void updateBabyProductWithEmptyNameThrowsExceptionTest() {
        // Given
        updateRequest.setName("   ");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> babyProductService.updateBabyProduct(1L, updateRequest)
        );
        assertEquals("Product name cannot be empty", exception.getMessage());
    }

    @Test
    void updateBabyProductPartialUpdateSucceedsTest() {
        // Given
        UpdateBabyProductRequest partialUpdate = new UpdateBabyProductRequest();
        partialUpdate.setName("New Name Only");
        // Other fields are null
        
        when(babyProductRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(babyProductRepository.save(any(BabyProduct.class))).thenReturn(testProduct);

        // When
        BabyProductResponse response = babyProductService.updateBabyProduct(1L, partialUpdate);

        // Then
        assertNotNull(response);
        verify(babyProductRepository).save(any(BabyProduct.class));
    }

    @Test
    void updateBabyProductWithPriceHistorySucceedsTest() {
        // Given
        PriceHistoryDTO priceDTO = new PriceHistoryDTO();
        priceDTO.setRetailer("Walmart");
        priceDTO.setPrice(new BigDecimal("25.99"));
        priceDTO.setDate(LocalDateTime.now());
        
        updateRequest.setPriceHistory(Arrays.asList(priceDTO));
        
        when(babyProductRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(babyProductRepository.save(any(BabyProduct.class))).thenReturn(testProduct);

        // When
        BabyProductResponse response = babyProductService.updateBabyProduct(1L, updateRequest);

        // Then
        assertNotNull(response);
        verify(babyProductRepository).save(any(BabyProduct.class));
    }

    // ==================== DELETE TESTS ====================

    @Test
    void deleteByIdWithValidIdSucceedsTest() {
        // Given
        when(babyProductRepository.existsById(1L)).thenReturn(true);
        doNothing().when(babyProductRepository).deleteById(1L);

        // When
        babyProductService.deleteById(1L);

        // Then
        verify(babyProductRepository, times(1)).existsById(1L);
        verify(babyProductRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteByIdWithInvalidIdThrowsExceptionTest() {
        // Given
        when(babyProductRepository.existsById(999L)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> babyProductService.deleteById(999L)
        );
        assertEquals("BabyProduct not found with id 999", exception.getMessage());
        verify(babyProductRepository, never()).deleteById(anyLong());
    }

    // ==================== VALIDATION TESTS ====================

    @Test
    void validatePriceHistoryWithNegativePriceThrowsExceptionTest() {
        // Given
        PriceHistoryDTO invalidPrice = new PriceHistoryDTO();
        invalidPrice.setRetailer("Amazon");
        invalidPrice.setPrice(new BigDecimal("-10.00"));
        invalidPrice.setDate(LocalDateTime.now());
        
        createRequest.setPriceHistory(Arrays.asList(invalidPrice));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> babyProductService.createBabyProduct(createRequest)
        );
        assertEquals("Price cannot be negative", exception.getMessage());
    }

    @Test
    void validatePriceHistoryWithoutRetailerThrowsExceptionTest() {
        // Given
        PriceHistoryDTO invalidPrice = new PriceHistoryDTO();
        invalidPrice.setPrice(new BigDecimal("29.99"));
        invalidPrice.setDate(LocalDateTime.now());
        // retailer is null
        
        createRequest.setPriceHistory(Arrays.asList(invalidPrice));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> babyProductService.createBabyProduct(createRequest)
        );
        assertEquals("Retailer is required for price history", exception.getMessage());
    }

    @Test
    void validatePriceHistoryWithoutPriceThrowsExceptionTest() {
        // Given
        PriceHistoryDTO invalidPrice = new PriceHistoryDTO();
        invalidPrice.setRetailer("Amazon");
        invalidPrice.setDate(LocalDateTime.now());
        // price is null
        
        createRequest.setPriceHistory(Arrays.asList(invalidPrice));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> babyProductService.createBabyProduct(createRequest)
        );
        assertEquals("Price is required for price history", exception.getMessage());
    }

    @Test
    void createBabyProductWithNullRequestThrowsExceptionTest() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> babyProductService.createBabyProduct(null)
        );
        assertEquals("Request cannot be null", exception.getMessage());
    }

    @Test
    void updateBabyProductWithNullRequestThrowsExceptionTest() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> babyProductService.updateBabyProduct(1L, null)
        );
        assertEquals("Request cannot be null", exception.getMessage());
    }
}

