package com.nestuity.service.dto;

import java.util.List;

public class UpdateBabyProductRequest {
    private String name;
    private String brand;
    private String category;
    private String description;
    private String currency;
    private Boolean inStock;
    private List<PriceHistoryDTO> priceHistory;

    // Constructors
    public UpdateBabyProductRequest() {}

    // Getters
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getCurrency() { return currency; }
    public Boolean getInStock() { return inStock; }
    public List<PriceHistoryDTO> getPriceHistory() { return priceHistory; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setInStock(Boolean inStock) { this.inStock = inStock; }
    public void setPriceHistory(List<PriceHistoryDTO> priceHistory) { this.priceHistory = priceHistory; }
}

