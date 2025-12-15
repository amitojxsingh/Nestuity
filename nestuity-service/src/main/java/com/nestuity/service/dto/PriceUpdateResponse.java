package com.nestuity.service.dto;

/**
 * Response DTO for price update operations.
 */
public class PriceUpdateResponse {
    private boolean success;
    private String message;
    private Long productId;
    private boolean newProduct;

    // Constructors
    public PriceUpdateResponse() {}

    public PriceUpdateResponse(boolean success, String message, Long productId, boolean newProduct) {
        this.success = success;
        this.message = message;
        this.productId = productId;
        this.newProduct = newProduct;
    }

    // Static factory methods
    public static PriceUpdateResponse success(Long productId, boolean newProduct, String productName) {
        String message = newProduct
                ? "Created new product and added price history: " + productName
                : "Updated price history for existing product: " + productName;
        return new PriceUpdateResponse(true, message, productId, newProduct);
    }

    public static PriceUpdateResponse error(String message) {
        return new PriceUpdateResponse(false, message, null, false);
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Long getProductId() { return productId; }
    public boolean isNewProduct() { return newProduct; }

    // Setters
    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
    public void setProductId(Long productId) { this.productId = productId; }
    public void setNewProduct(boolean newProduct) { this.newProduct = newProduct; }
}
