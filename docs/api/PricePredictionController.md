# Price Prediction API Documentation

## Overview

The Price Prediction API manages historical and real-time price predictions associated with baby products.
It allows clients to create predictions, retrieve individual or filtered predictions, update records, and delete data.
This API is consumed by services that analyze price trends, confidence ranges, and product-specific forecasts.

---

# Endpoints

---

## 1. **Create a Price Prediction**

**POST** `/api/price-predictions`

### Description

Creates a new price prediction object.

### Request Body

| Field          | Type          | Required | Description                                |
| -------------- | ------------- | -------- | ------------------------------------------ |
| id             | Long          | No       | Auto-generated; ignored on create          |
| predictedPrice | Double        | Yes      | Predicted price value                      |
| confidence     | Float         | Yes      | Confidence percentage (0.0–1.0)            |
| trend          | String        | Yes      | Trend label (e.g., "UP", "DOWN", "STABLE") |
| predictionDate | ZonedDateTime | Yes      | Date of prediction                         |
| babyProduct    | BabyProduct   | Yes      | Associated product entity                  |

### Response

**201 Created**
Returns the created `PricePrediction` object.

---

## 2. **Get Prediction by ID**

**GET** `/api/price-predictions/{id}`

### Description

Retrieves a single prediction by its ID.

### Path Parameters

| Name | Type | Description          |
| ---- | ---- | -------------------- |
| id   | Long | ID of the prediction |

### Responses

* **200 OK** – Returns `PricePrediction`
* **404 Not Found**

---

## 3. **Get Latest Prediction for a Product**

**GET** `/api/price-predictions/latest?babyProductId={id}`

### Description

Returns the most recent prediction created for a given baby product.

### Query Parameters

| Name          | Type | Required | Description       |
| ------------- | ---- | -------- | ----------------- |
| babyProductId | Long | Yes      | ID of the product |

### Responses

* **200 OK**
* **404 Not Found**

---

## 4. **Get Predictions by Trend**

**GET** `/api/price-predictions/trend/{trend}`

### Description

Retrieves all predictions that match a specific trend (e.g., UP, DOWN, STABLE).

### Path Parameters

| Name  | Type   | Description |
| ----- | ------ | ----------- |
| trend | String | Trend label |

### Response

**200 OK** – Returns `List<PricePrediction>`

---

## 5. **Get Predictions by Confidence Threshold**

**GET** `/api/price-predictions/confidence?minConfidence={value}`

### Description

Returns predictions with confidence greater than or equal to the given threshold.

### Query Parameters

| Name          | Type  | Required | Description                  |
| ------------- | ----- | -------- | ---------------------------- |
| minConfidence | Float | Yes      | Minimum confidence (0.0–1.0) |

### Response

**200 OK** – Returns `List<PricePrediction>`

---

## 6. **Get Predictions by Date Range**

**GET** `/api/price-predictions/date-range?start={date}&end={date}`

### Description

Fetches predictions created between two dates.

### Query Parameters

| Name  | Type          | Required | Description |
| ----- | ------------- | -------- | ----------- |
| start | ZonedDateTime | Yes      | Start date  |
| end   | ZonedDateTime | Yes      | End date    |

### Response

**200 OK** – Returns `List<PricePrediction>`

---

## 7. **Update a Prediction**

**PUT** `/api/price-predictions/{id}`

### Description

Updates an existing price prediction.

### Path Parameters

| Name | Type | Description          |
| ---- | ---- | -------------------- |
| id   | Long | ID of the prediction |

### Request Body

Same fields as the Create endpoint.

### Responses

* **200 OK** – Returns updated prediction
* **404 Not Found**

---

## 8. **Delete a Prediction**

**DELETE** `/api/price-predictions/{id}`

### Description

Deletes a prediction by ID.

### Path Parameters

| Name | Type | Description                    |
| ---- | ---- | ------------------------------ |
| id   | Long | ID of the prediction to delete |

### Response

**204 No Content**

