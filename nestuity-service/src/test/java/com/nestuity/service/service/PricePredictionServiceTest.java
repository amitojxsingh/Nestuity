package com.nestuity.service.service;

import com.nestuity.service.entity.BabyProduct;
import com.nestuity.service.entity.PricePrediction;
import com.nestuity.service.repository.PricePredictionRepository;
import com.nestuity.service.service.PricePredictionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PricePredictionServiceTest {

    private PricePredictionRepository repository;
    private PricePredictionService service;

    private PricePrediction samplePrediction;
    private BabyProduct sampleProduct;

    @BeforeEach
    void setUp() {
        repository = mock(PricePredictionRepository.class);
        service = new PricePredictionService(repository);

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

    @Test
    void savePrediction_Success() {
        when(repository.save(any())).thenReturn(samplePrediction);
        PricePrediction saved = service.savePrediction(samplePrediction);
        assertNotNull(saved);
        assertEquals(1L, saved.getId());
    }

    @Test
    void getPredictionById_Found() {
        when(repository.findById(1L)).thenReturn(Optional.of(samplePrediction));
        Optional<PricePrediction> found = service.getPredictionById(1L);
        assertTrue(found.isPresent());
        assertEquals(10.5f, found.get().getPredictedPrice());
    }

    @Test
    void getPredictionById_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        Optional<PricePrediction> found = service.getPredictionById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void getLatestPrediction_Success() {
        when(repository.findFirstByBabyProductOrderByPredictionDateDesc(sampleProduct))
                .thenReturn(Optional.of(samplePrediction));
        Optional<PricePrediction> latest = service.getLatestPrediction(sampleProduct);
        assertTrue(latest.isPresent());
        assertEquals("increasing", latest.get().getTrend());
    }

    @Test
    void getByTrend_Success() {
        when(repository.findByTrend("increasing")).thenReturn(List.of(samplePrediction));
        List<PricePrediction> list = service.getPredictionsByTrend("increasing");
        assertEquals(1, list.size());
    }

    @Test
    void getByConfidence_Success() {
        when(repository.findByConfidenceGreaterThanEqual(0.8f)).thenReturn(List.of(samplePrediction));
        List<PricePrediction> list = service.getPredictionsByConfidence(0.8f);
        assertEquals(1, list.size());
    }

    @Test
    void getByDateRange_Success() {
        ZonedDateTime start = ZonedDateTime.now().minusDays(1);
        ZonedDateTime end = ZonedDateTime.now().plusDays(1);
        when(repository.findByPredictionDateBetween(start, end)).thenReturn(List.of(samplePrediction));
        List<PricePrediction> list = service.getPredictionsBetweenDates(start, end);
        assertEquals(1, list.size());
    }

    @Test
    void deletePrediction_Success() {
        doNothing().when(repository).deleteById(1L);
        service.deletePredictionById(1L);
        verify(repository, times(1)).deleteById(1L);
    }
}
