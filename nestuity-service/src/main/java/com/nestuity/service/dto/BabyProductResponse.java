package com.nestuity.service.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BabyProductResponse {
    private Long id;
    private String name;
    private String brand;
    private String category;
    private String description;
    private String currency;
    private Boolean inStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PriceHistoryDTO> priceHistory = new ArrayList<>();

    // Constructors
    public BabyProductResponse() {}

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getCurrency() { return currency; }
    public Boolean getInStock() { return inStock; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<PriceHistoryDTO> getPriceHistory() { return priceHistory; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setInStock(Boolean inStock) { this.inStock = inStock; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setPriceHistory(List<PriceHistoryDTO> priceHistory) { this.priceHistory = priceHistory; }
}

