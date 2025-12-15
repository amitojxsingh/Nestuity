# Baby Product API Documentation

This API manages baby-related products in the Nestuity system.
It provides endpoints to create, update, retrieve, list, and delete baby products.

---

## Base URL

```
/api/baby-products
```

---

# Endpoints

---

## 1. Create Baby Product

**POST** `/api/baby-products`

### Description

Creates a new baby product using the provided data.

### Request Body

`CreateBabyProductRequest`

| Field    | Type   | Description                 |
| -------- | ------ | --------------------------- |
| name     | String | Name of the baby product    |
| brand    | String | Product brand               |
| price    | Double | Product price               |
| category | String | Category or type of product |

*(Replace with actual fields in your DTO.)*

### Response

`201 Created`
Returns `BabyProductResponse`

---

## 2. Update Baby Product

**PUT** `/api/baby-products/{id}`

### Description

Updates an existing baby product with new details.

### Path Variables

| Name | Type | Description                 |
| ---- | ---- | --------------------------- |
| id   | Long | ID of the product to update |

### Request Body

`UpdateBabyProductRequest`

| Field    | Type   | Description      |
| -------- | ------ | ---------------- |
| name     | String | Updated name     |
| brand    | String | Updated brand    |
| price    | Double | Updated price    |
| category | String | Updated category |

### Response

`200 OK`
Returns `BabyProductResponse`

---

## 3. Get Baby Product by ID

**GET** `/api/baby-products/{id}`

### Description

Returns the baby product with the specified ID.

### Path Variables

| Name | Type | Description |
| ---- | ---- | ----------- |
| id   | Long | Product ID  |

### Response

`200 OK`
Returns `BabyProductResponse`

---

## 4. Get All Baby Products

**GET** `/api/baby-products`

### Description

Retrieves all baby products in the system.

### Response

`200 OK`
Returns `List<BabyProductResponse>`

---

## 5. Delete Baby Product

**DELETE** `/api/baby-products/{id}`

### Description

Deletes a baby product from the database.

### Path Variables

| Name | Type | Description                 |
| ---- | ---- | --------------------------- |
| id   | Long | ID of the product to delete |

### Response

`204 No Content`
