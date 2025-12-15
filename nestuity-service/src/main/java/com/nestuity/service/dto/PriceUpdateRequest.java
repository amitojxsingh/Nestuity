package com.nestuity.service.dto;

import java.math.BigDecimal;

/**
 * Request DTO for price updates from the scraper.
 */
public class PriceUpdateRequest {
    private String productUrl;
    private String retailer;
    private BigDecimal price;
    private String productName;
    private String brand;
    private String category;
    private String description;
    private Boolean inStock;
    private String currency;
    private String scrapedAt;  // ISO 8601 format

    // Constructors
    public PriceUpdateRequest() {}

    // Getters
    public String getProductUrl() { return productUrl; }
    public String getRetailer() { return retailer; }
    public BigDecimal getPrice() { return price; }
    public String getProductName() { return productName; }
    public String getBrand() { return brand; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public Boolean getInStock() { return inStock; }
    public String getCurrency() { return currency; }
    public String getScrapedAt() { return scrapedAt; }

    // Setters
    public void setProductUrl(String productUrl) { this.productUrl = productUrl; }
    public void setRetailer(String retailer) { this.retailer = retailer; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setInStock(Boolean inStock) { this.inStock = inStock; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setScrapedAt(String scrapedAt) { this.scrapedAt = scrapedAt; }
}
