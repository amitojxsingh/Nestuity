package com.nestuity.service.service;

import com.nestuity.service.dto.BabyProductResponse;
import com.nestuity.service.dto.CreateBabyProductRequest;
import com.nestuity.service.dto.PriceHistoryDTO;
import com.nestuity.service.dto.UpdateBabyProductRequest;
import com.nestuity.service.entity.BabyProduct;
import com.nestuity.service.entity.PriceHistory;
import com.nestuity.service.exception.ResourceNotFoundException;
import com.nestuity.service.repository.BabyProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BabyProductService {
    private final BabyProductRepository babyProductRepository;

    public BabyProductService(BabyProductRepository babyProductRepository) {
        this.babyProductRepository = babyProductRepository;
    }

    // ==================== CRUD Operations ====================

    // Create & Save Baby Product
    @Transactional
    public BabyProductResponse createBabyProduct(CreateBabyProductRequest request) {
        // Validate request
        validateCreateRequest(request);
        
        // Create entity from request
        BabyProduct entity = new BabyProduct();
        entity.setName(request.getName());
        entity.setBrand(request.getBrand());
        entity.setCategory(request.getCategory());
        entity.setDescription(request.getDescription());
        entity.setCurrency(request.getCurrency() != null ? request.getCurrency() : "CAD");
        entity.setInStock(request.getInStock() != null ? request.getInStock() : true);

        // Map price history
        if (request.getPriceHistory() != null && !request.getPriceHistory().isEmpty()) {
            for (PriceHistoryDTO priceDTO : request.getPriceHistory()) {
                validatePriceHistory(priceDTO);
                PriceHistory priceHistory = new PriceHistory();
                priceHistory.setRetailer(priceDTO.getRetailer());
                priceHistory.setProductUrl(priceDTO.getProductUrl());
                priceHistory.setPrice(priceDTO.getPrice());
                priceHistory.setDate(priceDTO.getDate() != null ? priceDTO.getDate() : LocalDateTime.now());
                entity.addPriceHistory(priceHistory);
            }
        }

        BabyProduct saved = babyProductRepository.save(entity);
        return toResponse(saved);
    }

    // Update ALL fields of Baby Product
    @Transactional
    public BabyProductResponse updateBabyProduct(Long id, UpdateBabyProductRequest request) {
        // Validate request
        validateUpdateRequest(request);
        
        return babyProductRepository.findById(id)
                .map(existing -> {
                    // Update basic fields
                    if (request.getName() != null) {
                        existing.setName(request.getName());
                    }
                    if (request.getBrand() != null) {
                        existing.setBrand(request.getBrand());
                    }
                    if (request.getCategory() != null) {
                        existing.setCategory(request.getCategory());
                    }
                    if (request.getDescription() != null) {
                        existing.setDescription(request.getDescription());
                    }
                    if (request.getCurrency() != null) {
                        existing.setCurrency(request.getCurrency());
                    }
                    if (request.getInStock() != null) {
                        existing.setInStock(request.getInStock());
                    }

                    // Update price history if provided
                    if (request.getPriceHistory() != null) {
                        existing.getPriceHistory().clear();
                        for (PriceHistoryDTO priceDTO : request.getPriceHistory()) {
                            validatePriceHistory(priceDTO);
                            PriceHistory priceHistory = new PriceHistory();
                            priceHistory.setRetailer(priceDTO.getRetailer());
                            priceHistory.setProductUrl(priceDTO.getProductUrl());
                            priceHistory.setPrice(priceDTO.getPrice());
                            priceHistory.setDate(priceDTO.getDate() != null ? priceDTO.getDate() : LocalDateTime.now());
                            existing.addPriceHistory(priceHistory);
                        }
                    }

                    BabyProduct updated = babyProductRepository.save(existing);
                    return toResponse(updated);
                })
                .orElseThrow(() -> new ResourceNotFoundException("BabyProduct not found with id " + id));
    }

    // Get one product by its ID
    @Transactional(readOnly = true)
    public BabyProductResponse findById(Long id) {
        return babyProductRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("BabyProduct not found with id " + id));
    }

    // Get all products
    @Transactional(readOnly = true)
    public List<BabyProductResponse> findAll() {
        return babyProductRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get entity by ID (for internal use, returns entity not DTO)
    @Transactional(readOnly = true)
    public BabyProduct findEntityById(Long id) {
        return babyProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BabyProduct not found with id " + id));
    }

    // Delete a product
    @Transactional
    public void deleteById(Long id) {
        if (!babyProductRepository.existsById(id)) {
            throw new ResourceNotFoundException("BabyProduct not found with id " + id);
        }
        babyProductRepository.deleteById(id);
    }

    // ==================== Validation Methods ====================

    private void validateCreateRequest(CreateBabyProductRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (request.getName().length() > 255) {
            throw new IllegalArgumentException("Product name must not exceed 255 characters");
        }
        if (request.getBrand() != null && request.getBrand().length() > 255) {
            throw new IllegalArgumentException("Brand must not exceed 255 characters");
        }
        if (request.getCategory() != null && request.getCategory().length() > 100) {
            throw new IllegalArgumentException("Category must not exceed 100 characters");
        }
        if (request.getCurrency() != null && request.getCurrency().length() > 10) {
            throw new IllegalArgumentException("Currency must not exceed 10 characters");
        }
    }

    private void validateUpdateRequest(UpdateBabyProductRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getName() != null) {
            if (request.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be empty");
            }
            if (request.getName().length() > 255) {
                throw new IllegalArgumentException("Product name must not exceed 255 characters");
            }
        }
        if (request.getBrand() != null && request.getBrand().length() > 255) {
            throw new IllegalArgumentException("Brand must not exceed 255 characters");
        }
        if (request.getCategory() != null && request.getCategory().length() > 100) {
            throw new IllegalArgumentException("Category must not exceed 100 characters");
        }
        if (request.getCurrency() != null && request.getCurrency().length() > 10) {
            throw new IllegalArgumentException("Currency must not exceed 10 characters");
        }
    }

    private void validatePriceHistory(PriceHistoryDTO priceDTO) {
        if (priceDTO == null) {
            throw new IllegalArgumentException("Price history entry cannot be null");
        }
        if (priceDTO.getRetailer() == null || priceDTO.getRetailer().trim().isEmpty()) {
            throw new IllegalArgumentException("Retailer is required for price history");
        }
        if (priceDTO.getPrice() == null) {
            throw new IllegalArgumentException("Price is required for price history");
        }
        if (priceDTO.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    // ==================== Mapper Methods ====================

    private BabyProductResponse toResponse(BabyProduct entity) {
        BabyProductResponse response = new BabyProductResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setBrand(entity.getBrand());
        response.setCategory(entity.getCategory());
        response.setDescription(entity.getDescription());
        response.setCurrency(entity.getCurrency());
        response.setInStock(entity.getInStock());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        // Map price history
        if (entity.getPriceHistory() != null) {
            List<PriceHistoryDTO> priceHistoryDTOs = entity.getPriceHistory().stream()
                    .map(this::toPriceHistoryDTO)
                    .collect(Collectors.toList());
            response.setPriceHistory(priceHistoryDTOs);
        }

        return response;
    }

    private PriceHistoryDTO toPriceHistoryDTO(PriceHistory entity) {
        PriceHistoryDTO dto = new PriceHistoryDTO();
        dto.setRetailer(entity.getRetailer());
        dto.setProductUrl(entity.getProductUrl());
        dto.setPrice(entity.getPrice());
        dto.setDate(entity.getDate());
        return dto;
    }
}
