package com.nestuity.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nestuity.service.dto.PriceUpdateRequest;
import com.nestuity.service.dto.PriceUpdateResponse;
import com.nestuity.service.service.PriceUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for PriceUpdateController.
 */
@WebMvcTest(PriceUpdateController.class)
class PriceUpdateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceUpdateService priceUpdateService;

    @Autowired
    private ObjectMapper objectMapper;

    private PriceUpdateRequest request;

    @BeforeEach
    void setup() {
        request = new PriceUpdateRequest();
        request.setProductUrl("http://example.com/product1");
        request.setPrice(new BigDecimal("19.99"));
        request.setCurrency("USD");
    }

    @Test
    void healthEndpointReturns200() throws Exception {
        mockMvc.perform(get("/api/price-updates/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Price update service is running"));
    }

    @Test
    void updatePriceReturns200ForExistingProduct() throws Exception {
        PriceUpdateResponse response = new PriceUpdateResponse(true, "Updated existing product", 1L, false);
        when(priceUpdateService.processPriceUpdate(any(PriceUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/price-updates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.newProduct").value(false))
                .andExpect(jsonPath("$.message").value("Updated existing product"));
    }

    @Test
    void updatePriceReturns201ForNewProduct() throws Exception {
        PriceUpdateResponse response = new PriceUpdateResponse(true, "Created new product", 2L, true);
        when(priceUpdateService.processPriceUpdate(any(PriceUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/price-updates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.newProduct").value(true))
                .andExpect(jsonPath("$.message").value("Created new product"));
    }

    @Test
    void updatePriceReturns400ForFailure() throws Exception {
        PriceUpdateResponse response = new PriceUpdateResponse(false, "Invalid data", null, false);
        when(priceUpdateService.processPriceUpdate(any(PriceUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/price-updates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid data"));
    }
}
