# Inventory API Documentation

This API manages inventory items associated with users (e.g., diapers, wipes, formula, etc.).
It supports full CRUD operations, as well as user-specific queries, quantity calculations, and updates.

---

## Base URL

```
/api/inventory
```

---

# Endpoints

---

# 1. CREATE

---

## 1.1 Create Inventory Item for a User

**POST** `/api/inventory/user/{userId}`

### Description

Creates a new inventory item for a user.
Automatically sets `preferredSupplyMin` to 14 if not provided.

### Path Variables

| Name   | Type | Description    |
| ------ | ---- | -------------- |
| userId | Long | ID of the user |

### Request Body

`Inventory` object
(Your entity fields will appear here in the final report.)

### Response

`200 OK` → Created `Inventory`
`404 Not Found` → User not found

---

# 2. READ

---

## 2.1 Get All Inventory Items

**GET** `/api/inventory`

### Description

Returns all inventory items in the system.

### Response

`200 OK` → `List<Inventory>`

---

## 2.2 Get Inventory Item by ID

**GET** `/api/inventory/{inventoryId}`

### Description

Fetch a specific inventory item.

### Path Variables

| Name        | Type | Description              |
| ----------- | ---- | ------------------------ |
| inventoryId | Long | ID of the inventory item |

### Response

`200 OK` → `Inventory`
`404 Not Found`

---

## 2.3 Get All Inventory Items for a User

**GET** `/api/inventory/user/{userId}`

### Description

Returns all inventory items belonging to a specific user.

### Path Variables

| Name   | Type | Description    |
| ------ | ---- | -------------- |
| userId | Long | ID of the user |

### Response

`200 OK` → `List<Inventory>`
`404 Not Found` → If user not found

---

## 2.4 Get a Specific Inventory Item for a User by Supply Name

**GET** `/api/inventory/user/{userId}/{supplyName}`

### Description

Fetch a specific inventory item for a user based on the supply name
(e.g., “diapers”, “formula”, etc.).

### Path Variables

| Name       | Type   | Description                                             |
| ---------- | ------ | ------------------------------------------------------- |
| userId     | Long   | User ID                                                 |
| supplyName | String | Supply name (case-sensitive depending on service logic) |

### Response

`200 OK` → `Inventory`
`404 Not Found`

---

## 2.5 Get Total Single Quantity (Individual Units)

**GET** `/api/inventory/user/{userId}/{supplyName}/single-quantity`

### Description

Returns the total number of *individual* items
(e.g., number of diapers, number of wipes).

### Path Variables

| Name       | Type   | Description |
| ---------- | ------ | ----------- |
| userId     | Long   | User ID     |
| supplyName | String | Supply name |

### Response

`200 OK` → `Double` (total individual units)
`404 Not Found`

---

## 2.6 Get Total Unit Quantity (Boxes/Packages)

**GET** `/api/inventory/user/{userId}/{supplyName}/unit-quantity`

### Description

Returns the number of *units* (e.g., boxes or packages of diapers).

### Path Variables

| Name       | Type   | Description |
| ---------- | ------ | ----------- |
| userId     | Long   | User ID     |
| supplyName | String | Supply name |

### Response

`200 OK` → `Double` (total units)
`404 Not Found`

---

# 3. UPDATE

---

## 3.1 Update Inventory by ID

**PUT** `/api/inventory/{inventoryId}`

### Description

Updates an existing inventory item.
If `preferredSupplyMin` is missing, defaults to 14.

### Path Variables

| Name        | Type | Description  |
| ----------- | ---- | ------------ |
| inventoryId | Long | Inventory ID |

### Request Body

`Inventory` — fields to update

### Response

`200 OK` → Updated `Inventory`
`404 Not Found`

---

## 3.2 Update Single Quantity for User Supply

**PUT** `/api/inventory/user/{userId}/{supplyName}/single-quantity`

### Description

Updates the total number of individual items for a given supply.

### Path Variables

| Name       | Type   | Description |
| ---------- | ------ | ----------- |
| userId     | Long   | User ID     |
| supplyName | String | Supply name |

### Request Body

JSON object:

```json
{
  "totalSingleQuantity": 42.0
}
```

### Response

`200 OK` → Updated `Inventory`
`400 Bad Request` → Missing or negative quantity
`404 Not Found`

---

## 3.3 Update Unit Quantity for User Supply

**PUT** `/api/inventory/user/{userId}/{supplyName}/unit-quantity`

### Description

Updates the number of units (boxes/packages) for a given supply.

### Path Variables

| Name       | Type   | Description |
| ---------- | ------ | ----------- |
| userId     | Long   | User ID     |
| supplyName | String | Supply name |

### Request Body

```json
{
  "totalUnitQuantity": 4.0
}
```

### Response

`200 OK` → Updated `Inventory`
`400 Bad Request`
`404 Not Found`

---

# 4. DELETE

---

## 4.1 Delete Inventory Item

**DELETE** `/api/inventory/{inventoryId}`

### Description

Deletes an inventory item by its ID.

### Path Variables

| Name        | Type | Description  |
| ----------- | ---- | ------------ |
| inventoryId | Long | Inventory ID |

### Response

`204 No Content`
`404 Not Found`
