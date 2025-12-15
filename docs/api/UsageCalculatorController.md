# Usage Calculator API Documentation

## Overview

The Usage Calculator API allows updating baby information and diaper inventory based on user input.
It is primarily used when editing baby data and recalculating expected diaper usage.

---

# Endpoints

---

## 1. Update Usage Calculator Data

**POST** `/api/usage-calculator/edit`

### Description

Updates baby information and diaper inventory using the provided data.
The request triggers recalculation of diaper usage according to the updated baby details.

### Request Body (`UsageCalculatorRequest`)

| Field            | Type    | Description                                           |
| ---------------- | ------- | ----------------------------------------------------- |
| babyId           | Long    | ID of the baby being updated                          |
| weight           | Double  | Baby's weight in kilograms                            |
| age              | Integer | Baby's age in months                                  |
| dailyDiaperUsage | Integer | Optional: manually entered daily diaper usage         |
| diaperSize       | String  | Optional: diaper size (e.g., "1", "2", "3")           |
| otherFields      | …       | Additional optional fields as required by the service |

*(Adjust field list based on actual `UsageCalculatorRequest` DTO structure.)*

### Response Body

| Field   | Type   | Description          |
| ------- | ------ | -------------------- |
| message | String | Success confirmation |

### Response Codes

| Status              | Meaning                                      |
| ------------------- | -------------------------------------------- |
| **200 OK**          | Usage calculator updated successfully        |
| **400 Bad Request** | Validation failed or required fields missing |

### Example Request

```json
{
  "babyId": 42,
  "weight": 5.3,
  "age": 3,
  "dailyDiaperUsage": 8,
  "diaperSize": "2"
}
```

### Example Response

```json
"Usage calculator updated successfully"
```
