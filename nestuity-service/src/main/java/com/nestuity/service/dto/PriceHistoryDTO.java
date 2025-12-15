package com.nestuity.service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PriceHistoryDTO {
    private String retailer;
    private String productUrl;
    private BigDecimal price;
    private LocalDateTime date;

    // Constructors
    public PriceHistoryDTO() {}

    public PriceHistoryDTO(String retailer, String productUrl, BigDecimal price, LocalDateTime date) {
        this.retailer = retailer;
        this.productUrl = productUrl;
        this.price = price;
        this.date = date;
    }

    // Getters
    public String getRetailer() { return retailer; }
    public String getProductUrl() { return productUrl; }
    public BigDecimal getPrice() { return price; }
    public LocalDateTime getDate() { return date; }

    // Setters
    public void setRetailer(String retailer) { this.retailer = retailer; }
    public void setProductUrl(String productUrl) { this.productUrl = productUrl; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setDate(LocalDateTime date) { this.date = date; }
}

