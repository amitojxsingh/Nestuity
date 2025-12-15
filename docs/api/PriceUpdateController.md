# Price Update API Documentation

## Overview

The Price Update API is used by the external scraper service to send updated product pricing information to the backend.
If a price update references a product URL that does not yet exist in the system, a new BabyProduct entry is created.
Otherwise, the price is appended to the product’s price history.

This controller also provides a basic health-check endpoint for monitoring.

---

# Endpoints

---

## 1. Receive and Process a Price Update

**POST** `/api/price-updates`

### Description

Receives price update data from the scraper service.
The backend automatically determines whether the URL corresponds to an existing product:

* If **new product** → creates a new product and inserts the initial price.
* If **existing product** → appends a new price history record.

The response includes details about what action was taken.

### Request Body (`PriceUpdateRequest`)

| Field        | Type   | Description                                   |
| ------------ | ------ | --------------------------------------------- |
| productUrl   | String | URL of the scraped product                    |
| productName  | String | Name of the product (from scraper)            |
| currentPrice | Double | The scraped price                             |
| imageUrl     | String | Image URL scraped for the product             |
| source       | String | Source identifier (e.g., "Amazon", "Walmart") |

*(Fields listed assuming typical structure — adjust if needed.)*

### Response Body (`PriceUpdateResponse`)

| Field      | Type    | Description                                   |
| ---------- | ------- | --------------------------------------------- |
| success    | boolean | Whether the update was processed successfully |
| newProduct | boolean | True if a new product was created             |
| productId  | Long    | ID of the created or updated product          |
| message    | String  | Informational message (created/updated/error) |

### Response Codes

| Status              | Meaning                                  |
| ------------------- | ---------------------------------------- |
| **201 Created**     | A new product was created                |
| **200 OK**          | A price was added to an existing product |
| **400 Bad Request** | Invalid payload or processing error      |

### Example Request

```json
{
  "productUrl": "https://www.amazon.ca/dp/ABC123",
  "productName": "Pampers Swaddlers Size 2",
  "currentPrice": 18.99,
  "imageUrl": "https://images.amazon.com/pampers.jpg",
  "source": "Amazon"
}
```

### Example Responses

**New product created (201):**

```json
{
  "success": true,
  "newProduct": true,
  "productId": 42,
  "message": "New product created and price recorded."
}
```

**Existing product updated (200):**

```json
{
  "success": true,
  "newProduct": false,
  "productId": 42,
  "message": "Price updated successfully."
}
```

**Error (400):**

```json
{
  "success": false,
  "newProduct": false,
  "productId": null,
  "message": "Invalid product URL."
}
```

---

## 2. Service Health Check

**GET** `/api/price-updates/health`

### Description

Simple endpoint used for monitoring or uptime checks.

### Response (200 OK)

```
Price update service is running
```
