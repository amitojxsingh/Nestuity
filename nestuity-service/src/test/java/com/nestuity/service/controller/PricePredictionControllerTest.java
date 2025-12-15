package com.nestuity.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nestuity.service.controller.PricePredictionController;
import com.nestuity.service.entity.BabyProduct;
import com.nestuity.service.entity.PricePrediction;
import com.nestuity.service.service.BabyProductService;
import com.nestuity.service.service.PricePredictionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PricePredictionController.class)
class PricePredictionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PricePredictionService pricePredictionService;

    @MockBean
    private BabyProductService babyProductService;

    private ObjectMapper objectMapper;
    private PricePrediction samplePrediction;
    private BabyProduct sampleProduct;



    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleProduct = new BabyProduct();
        sampleProduct.setId(1L);
        sampleProduct.setName("Baby Bottle");

        samplePrediction = new PricePrediction();
        samplePrediction.setId(1L);
        samplePrediction.setBabyProduct(sampleProduct);
        samplePrediction.setPredictedPrice(10.5f);
        samplePrediction.setConfidence(0.9f);
        samplePrediction.setTrend("increasing");
        samplePrediction.setPredictionDate(ZonedDateTime.now());
        samplePrediction.setFactors(new ArrayList<>(List.of("seasonal demand")));
        samplePrediction.setCreatedAt(ZonedDateTime.now());
    }

    // ---------------- CREATE ----------------
    @Test
    void createPrediction_Success() throws Exception {
        when(pricePredictionService.savePrediction(any())).thenReturn(samplePrediction);

        mockMvc.perform(post("/api/price-predictions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(samplePrediction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.trend").value("increasing"));
    }

    // ---------------- READ BY ID ----------------
    @Test
    void getPredictionById_Success() throws Exception {
        when(pricePredictionService.getPredictionById(1L)).thenReturn(Optional.of(samplePrediction));

        mockMvc.perform(get("/api/price-predictions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.predictedPrice").value(10.5));
    }

    @Test
    void getPredictionById_NotFound() throws Exception {
        when(pricePredictionService.getPredictionById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/price-predictions/999"))
                .andExpect(status().isNotFound());
    }

    // ---------------- GET LATEST ----------------
    @Test
    void getLatestForProduct_Success() throws Exception {
        when(babyProductService.findEntityById(1L)).thenReturn(sampleProduct);
        when(pricePredictionService.getLatestPrediction(sampleProduct)).thenReturn(Optional.of(samplePrediction));

        mockMvc.perform(get("/api/price-predictions/latest")
                        .param("babyProductId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getLatestForProduct_NotFound() throws Exception {
        when(babyProductService.findEntityById(1L)).thenReturn(sampleProduct);
        when(pricePredictionService.getLatestPrediction(sampleProduct)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/price-predictions/latest")
                        .param("babyProductId", "1"))
                .andExpect(status().isNotFound());
    }

    // ---------------- GET BY TREND ----------------
    @Test
    void getByTrend_Success() throws Exception {
        when(pricePredictionService.getPredictionsByTrend("increasing"))
                .thenReturn(List.of(samplePrediction));

        mockMvc.perform(get("/api/price-predictions/trend/increasing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trend").value("increasing"));
    }

    // ---------------- GET BY CONFIDENCE ----------------
    @Test
    void getByConfidence_Success() throws Exception {
        when(pricePredictionService.getPredictionsByConfidence(0.8f))
                .thenReturn(List.of(samplePrediction));

        mockMvc.perform(get("/api/price-predictions/confidence")
                        .param("minConfidence", "0.8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].confidence").value(0.9));
    }

    // ---------------- GET BY DATE RANGE ----------------
    @Test
    void getByDateRange_Success() throws Exception {
        ZonedDateTime start = ZonedDateTime.now().minusDays(1);
        ZonedDateTime end = ZonedDateTime.now().plusDays(1);

        when(pricePredictionService.getPredictionsBetweenDates(start, end))
                .thenReturn(List.of(samplePrediction));

        mockMvc.perform(get("/api/price-predictions/date-range")
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // ---------------- UPDATE ----------------
    @Test
    void updatePrediction_Success() throws Exception {
        when(pricePredictionService.getPredictionById(1L)).thenReturn(Optional.of(samplePrediction));
        when(pricePredictionService.savePrediction(any())).thenReturn(samplePrediction);

        mockMvc.perform(put("/api/price-predictions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(samplePrediction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updatePrediction_NotFound() throws Exception {
        when(pricePredictionService.getPredictionById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/price-predictions/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(samplePrediction)))
                .andExpect(status().isNotFound());
    }

    // ---------------- DELETE ----------------
    @Test
    void deletePrediction_Success() throws Exception {
        doNothing().when(pricePredictionService).deletePredictionById(1L);

        mockMvc.perform(delete("/api/price-predictions/1"))
                .andExpect(status().isNoContent());
    }
}
