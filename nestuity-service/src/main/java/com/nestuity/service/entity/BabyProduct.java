package com.nestuity.service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "baby_product")
public class BabyProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String brand;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 10)
    private String currency;

    @Column(name = "in_stock")
    private Boolean inStock;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "babyProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceHistory> priceHistory = new ArrayList<>();

    // Triggers before the entity is stored for the first time
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    //  Triggers before the entity is updated in the database
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public BabyProduct() {}

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
    public List<PriceHistory> getPriceHistory() { return priceHistory; }

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
    public void setPriceHistory(List<PriceHistory> priceHistory) { this.priceHistory = priceHistory; }

    // Utility method to add price history
    public void addPriceHistory(PriceHistory price) {
        priceHistory.add(price);
        price.setBabyProduct(this);
    }

    // Utility method to remove price history
    public void removePriceHistory(PriceHistory price) {
        priceHistory.remove(price);
        price.setBabyProduct(null);
    }
}
