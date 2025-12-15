package com.nestuity.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nestuity.service.dto.BabyProductResponse;
import com.nestuity.service.dto.CreateBabyProductRequest;
import com.nestuity.service.dto.PriceHistoryDTO;
import com.nestuity.service.dto.UpdateBabyProductRequest;
import com.nestuity.service.exception.ResourceNotFoundException;
import com.nestuity.service.service.BabyProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BabyProductController.class)
class BabyProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BabyProductService babyProductService;

    private ObjectMapper objectMapper;
    private BabyProductResponse testResponse;
    private CreateBabyProductRequest createRequest;
    private UpdateBabyProductRequest updateRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Setup test response
        testResponse = new BabyProductResponse();
        testResponse.setId(1L);
        testResponse.setName("Test Product");
        testResponse.setBrand("Test Brand");
        testResponse.setCategory("Feeding");
        testResponse.setDescription("Test Description");
        testResponse.setCurrency("CAD");
        testResponse.setInStock(true);
        testResponse.setCreatedAt(LocalDateTime.now());
        testResponse.setUpdatedAt(LocalDateTime.now());

        // Setup create request
        createRequest = new CreateBabyProductRequest();
        createRequest.setName("New Product");
        createRequest.setBrand("New Brand");
        createRequest.setCategory("Feeding");
        createRequest.setCurrency("CAD");
        createRequest.setInStock(true);

        // Setup update request
        updateRequest = new UpdateBabyProductRequest();
        updateRequest.setName("Updated Product");
        updateRequest.setBrand("Updated Brand");
    }

    // ==================== CREATE TESTS ====================

    @Test
    void createBabyProductReturns201CreatedTest() throws Exception {
        // Given
        when(babyProductService.createBabyProduct(any(CreateBabyProductRequest.class)))
            .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(post("/api/baby-products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.brand").value("Test Brand"));

        verify(babyProductService, times(1)).createBabyProduct(any(CreateBabyProductRequest.class));
    }

    @Test
    void createBabyProductWithPriceHistoryReturns201Test() throws Exception {
        // Given
        PriceHistoryDTO priceDTO = new PriceHistoryDTO();
        priceDTO.setRetailer("Amazon");
        priceDTO.setPrice(new BigDecimal("29.99"));
        priceDTO.setProductUrl("https://amazon.ca/product/123");
        priceDTO.setDate(LocalDateTime.now());
        
        createRequest.setPriceHistory(Arrays.asList(priceDTO));
        testResponse.setPriceHistory(Arrays.asList(priceDTO));
        
        when(babyProductService.createBabyProduct(any(CreateBabyProductRequest.class)))
            .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(post("/api/baby-products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.priceHistory").isArray())
                .andExpect(jsonPath("$.priceHistory[0].retailer").value("Amazon"));
    }

    @Test
    void createBabyProductWithInvalidDataReturns400Test() throws Exception {
        // Given
        when(babyProductService.createBabyProduct(any(CreateBabyProductRequest.class)))
            .thenThrow(new IllegalArgumentException("Product name is required"));

        // When & Then
        mockMvc.perform(post("/api/baby-products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Product name is required"));
    }

    // ==================== READ TESTS ====================

    @Test
    void getBabyProductByIdReturns200Test() throws Exception {
        // Given
        when(babyProductService.findById(1L)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/baby-products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(babyProductService, times(1)).findById(1L);
    }

    @Test
    void getBabyProductByIdWithInvalidIdReturns404Test() throws Exception {
        // Given
        when(babyProductService.findById(999L))
            .thenThrow(new ResourceNotFoundException("BabyProduct not found with id 999"));

        // When & Then
        mockMvc.perform(get("/api/baby-products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("BabyProduct not found with id 999"));
    }

    @Test
    void getAllBabyProductsReturns200Test() throws Exception {
        // Given
        BabyProductResponse response2 = new BabyProductResponse();
        response2.setId(2L);
        response2.setName("Product 2");
        
        when(babyProductService.findAll()).thenReturn(Arrays.asList(testResponse, response2));

        // When & Then
        mockMvc.perform(get("/api/baby-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(babyProductService, times(1)).findAll();
    }

    @Test
    void getAllBabyProductsReturnsEmptyArrayTest() throws Exception {
        // Given
        when(babyProductService.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/baby-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ==================== UPDATE TESTS ====================

    @Test
    void updateBabyProductReturns200Test() throws Exception {
        // Given
        when(babyProductService.updateBabyProduct(eq(1L), any(UpdateBabyProductRequest.class)))
            .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(put("/api/baby-products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(babyProductService, times(1)).updateBabyProduct(eq(1L), any(UpdateBabyProductRequest.class));
    }

    @Test
    void updateBabyProductWithInvalidIdReturns404Test() throws Exception {
        // Given
        when(babyProductService.updateBabyProduct(eq(999L), any(UpdateBabyProductRequest.class)))
            .thenThrow(new ResourceNotFoundException("BabyProduct not found with id 999"));

        // When & Then
        mockMvc.perform(put("/api/baby-products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("BabyProduct not found with id 999"));
    }

    @Test
    void updateBabyProductWithInvalidDataReturns400Test() throws Exception {
        // Given
        when(babyProductService.updateBabyProduct(eq(1L), any(UpdateBabyProductRequest.class)))
            .thenThrow(new IllegalArgumentException("Product name cannot be empty"));

        // When & Then
        mockMvc.perform(put("/api/baby-products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Product name cannot be empty"));
    }

    @Test
    void updateBabyProductPartialUpdateSucceedsTest() throws Exception {
        // Given
        UpdateBabyProductRequest partialUpdate = new UpdateBabyProductRequest();
        partialUpdate.setName("New Name Only");
        
        when(babyProductService.updateBabyProduct(eq(1L), any(UpdateBabyProductRequest.class)))
            .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(put("/api/baby-products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk());
    }

    // ==================== DELETE TESTS ====================

    @Test
    void deleteBabyProductReturns204Test() throws Exception {
        // Given
        doNothing().when(babyProductService).deleteById(1L);

        // When & Then
        mockMvc.perform(delete("/api/baby-products/1"))
                .andExpect(status().isNoContent());

        verify(babyProductService, times(1)).deleteById(1L);
    }

    @Test
    void deleteBabyProductWithInvalidIdReturns404Test() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("BabyProduct not found with id 999"))
            .when(babyProductService).deleteById(999L);

        // When & Then
        mockMvc.perform(delete("/api/baby-products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("BabyProduct not found with id 999"));
    }

    // ==================== EDGE CASE TESTS ====================

    @Test
    void createBabyProductWithMaxLengthFieldsSucceedsTest() throws Exception {
        // Given
        createRequest.setName("a".repeat(255));
        createRequest.setBrand("b".repeat(255));
        
        when(babyProductService.createBabyProduct(any(CreateBabyProductRequest.class)))
            .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(post("/api/baby-products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void getBabyProductWithNegativeIdReturns404Test() throws Exception {
        // Given
        when(babyProductService.findById(-1L))
            .thenThrow(new ResourceNotFoundException("BabyProduct not found with id -1"));

        // When & Then
        mockMvc.perform(get("/api/baby-products/-1"))
                .andExpect(status().isNotFound());
    }
}

