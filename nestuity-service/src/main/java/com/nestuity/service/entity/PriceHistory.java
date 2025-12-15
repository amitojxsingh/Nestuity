package com.nestuity.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_history")
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "baby_product_id", nullable = false)
    @JsonIgnore
    private BabyProduct babyProduct;

    @Column(nullable = false)
    private String retailer;

    @Column(name = "product_url", length = 2048)
    private String productUrl;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime date;

    // Constructors
    public PriceHistory() {}

    public PriceHistory(BabyProduct babyProduct, String retailer, String productUrl, BigDecimal price, LocalDateTime date) {
        this.babyProduct = babyProduct;
        this.retailer = retailer;
        this.productUrl = productUrl;
        this.price = price;
        this.date = date;
    }

    // Getters
    public Long getId() { return id; }
    public BabyProduct getBabyProduct() { return babyProduct; }
    public String getRetailer() { return retailer; }
    public String getProductUrl() { return productUrl; }
    public BigDecimal getPrice() { return price; }
    public LocalDateTime getDate() { return date; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setBabyProduct(BabyProduct babyProduct) { this.babyProduct = babyProduct; }
    public void setRetailer(String retailer) { this.retailer = retailer; }
    public void setProductUrl(String productUrl) { this.productUrl = productUrl; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setDate(LocalDateTime date) { this.date = date; }
}

