# Baby Product Management System - API Documentation

## Base URL
```
http://localhost:8080/api/
http://[cybera-ipv6-address]/api/
```

## HTTP Status Codes
- **200 OK**: Successful GET, PUT requests
- **201 Created**: Successful POST requests
- **204 No Content**: Successful DELETE requests
- **400 Bad Request**: Invalid request data
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

## User Management API Endpoints

### User Information
- **POST** `/api/users`
  - Description: Create a new user
  - Request Body: User object with personal details
  - Response: Created user object
  - Notes: Returns 409 if email already exists

- **GET** `/api/users`
  - Description: Retrieve all users
  - Response: Array of user objects

- **GET** `/api/users/{id}`
  - Description: Get user information by ID
  - Parameters: `id` (path parameter)
  - Response: User object with personal details

- **PUT** `/api/users/{id}`
  - Description: Update user information
  - Parameters: `id` (path parameter)
  - Request Body: Updated user data
  - Response: Updated user object

- **DELETE** `/api/users/{id}`
  - Description: Delete a user account and associated preferences
  - Parameters: `id` (path parameter)
  - Response: No content (204)

- **GET** `/api/users/email/{email}`
  - Description: Get user information by email address
  - Parameters: `email` (path parameter)
  - Response: User object with personal details

- **PUT** `/api/users/profile`
  - Description: Update user information
  - Request Body: User update data
  - Response: Updated user object

### Baby Management
- **GET** `/api/users/{userId}/babies`
  - Description: Get baby information for a specific user
  - Parameters: `userId` (path parameter)
  - Response: Array of baby objects

- **POST** `/api/users/babies`
  - Description: Add a new baby to user account
  - Request Body: Baby information object
  - Response: Created baby object

- **PUT** `/api/users/babies`
  - Description: Update baby information
  - Request Body: Updated baby information
  - Response: Updated baby object

- **DELETE** `/api/babies/{babyId}`
  - Description: Delete baby record
  - Parameters: `babyId` (path parameter)
  - Response: Success confirmation

## Baby Product Management API Endpoints

### Product Operations
- **GET** `api/baby-products`
  - Description: Get all baby products
  - Response: Array of baby product objects

- **GET** `api/baby-products/{id}`
  - Description: Get specific baby product by ID
  - Parameters: `id` (path parameter)
  - Response: Baby product object

- **POST** `api/baby-products`
  - Description: Create a new baby product
  - Request Body: Product data object
  - Response: Created product object

- **PUT** `api/baby-products/{id}`
  - Description: Update baby product information
  - Parameters: `id` (path parameter)
  - Request Body: Updated product data
  - Response: Updated product object

- **DELETE** `api/baby-products/{id}`
  - Description: Delete a baby product
  - Parameters: `id` (path parameter)
  - Response: Success confirmation

### Price Management & Predictions
- **PUT** `api/baby-products/{productId}/price`
  - Description: Update product price
  - Parameters: `productId` (path parameter)
  - Request Body: Price update object
  - Response: Updated product with new price

- **POST** `api/price-predictions`
  - Description: Create a new price prediction entry for a baby product
  - Request Body: Prediction object (includes product, predicted price, confidence, trend, createdAt)
  - Response: Created prediction object

- **GET** `api/price-predictions/{id}`
  - Description: Retrieve a specific price prediction by ID
  - Parameters: `id` (path parameter)
  - Response: Price prediction object

- **GET** `api/price-predictions/latest`
  - Description: Get the most recent prediction for a given baby product
  - Query Parameters: `babyProductId` (ID of the baby product)
  - Response: Latest price prediction object

- **GET** `api/price-predictions/trend/{trend}`
  - Description: Retrieve all predictions filtered by trend type
  - Parameters: `trend` (path parameter, e.g., increasing, decreasing, stable)
  - Response: List of predictions matching the trend

- **GET** `api/price-predictions/confidence`
  - Description: Retrieve predictions with a confidence score above a threshold
  - Query Parameters: `minConfidence` (minimum confidence value)
  - Response: List of predictions above threshold

- **GET** `api/price-predictions/date-range`
  - Description: Retrieve predictions made within a specific date range
  - Query Parameters: 
    - `start` (ISO date)
    - `end` (ISO date)
  - Response: List of predictions in date range

- **PUT** `api/price-predictions/{id}`
  - Description: Update an existing price prediction
  - Parameters: `id` (path parameter)
  - Request Body: Updated prediction object
  - Response: Updated prediction object

- **DELETE** `api/price-predictions/{id}`
  - Description: Delete a price prediction entry by ID
  - Parameters: `id` (path parameter)
  - Response: No content (204)

### Baby Reminders
- **GET** `api/baby-reminders/{babyId}`
  - Description: Retrieve all reminders associated with a specific baby
  - Parameters: `babyId` (path parameter)
  - Response: List of baby reminder objects

- **POST** `api/baby-reminders/generate/{babyId}`
  - Description: Generate new reminders for a baby based on an upcoming time window
  - Parameters: 
    - `babyId` (path parameter)
  - Query Parameters:
    - `daysAhead` (optional, default: 30) — number of days to generate reminders ahead of time
  - Response: No content (204)

- **PATCH** `api/baby-reminders/complete/{reminderId}`
  - Description: Mark a reminder as complete and automatically reschedule the next one
  - Parameters: `reminderId` (path parameter)
  - Response: No content (204)

### Baby Task Templates
- **POST** `api/task-templates`
  - Description: Create a new baby task template
  - Request Body: Task template object (includes task name, frequency, category, etc.)
  - Response: Created task template object

- **GET** `api/task-templates`
  - Description: Retrieve all existing baby task templates
  - Response: List of task template objects

- **GET** `api/task-templates/{id}`
  - Description: Retrieve a specific baby task template by ID
  - Parameters: `id` (path parameter)
  - Response: Task template object

- **PUT** `api/task-templates/{id}`
  - Description: Update an existing task template by replacing all fields
  - Parameters: `id` (path parameter)
  - Request Body: Full task template object
  - Response: Updated task template object

- **PATCH** `api/task-templates/{id}`
  - Description: Partially update an existing task template (only specific fields)
  - Parameters: `id` (path parameter)
  - Request Body: Partial task template object
  - Response: Updated task template object

- **DELETE** `api/task-templates/{id}`
  - Description: Delete a baby task template by ID
  - Parameters: `id` (path parameter)
  - Response: No content (204)

### Baby Vaccinations
- **GET** `api/baby-vaccinations`
  - Description: Retrieve all available baby vaccination records
  - Response: List of baby vaccination objects

- **GET** `api/baby-vaccinations/{id}`
  - Description: Retrieve a specific vaccination record by ID
  - Parameters: `id` (path parameter)
  - Response: Baby vaccination object

- **GET** `api/baby-vaccinations/for-age/{months}`
  - Description: Retrieve all vaccinations recommended up to a given baby age (in months)
  - Parameters: `months` (path parameter)
  - Response: List of vaccination objects for cumulative age range

- **GET** `api/baby-vaccinations/exact/{months}`
  - Description: Retrieve vaccinations recommended specifically for a given baby age (in months)
  - Parameters: `months` (path parameter)
  - Response: List of vaccination objects for exact age

### Developmental Milestones
- **GET** `/api/milestones`
  - Description: Retrieve all developmental milestones
  - Response: List of developmental milestone objects

- **GET** `/api/milestones/{id}`
  - Description: Retrieve a specific developmental milestone by ID
  - Parameters: `id` (path parameter)
  - Response: Developmental milestone object

- **GET** `/api/milestones/age/{weeks}`
  - Description: Retrieve all milestones up to a specific baby age in weeks
  - Parameters: `weeks` (path parameter)
  - Response: List of milestones up to the given age

- **GET** `/api/milestones/exact/{weeks}`
  - Description: Retrieve milestones for a specific baby age in weeks
  - Parameters: `weeks` (path parameter)
  - Response: List of milestones for the exact age

### User Watchlists
- **GET** `/users/{userId}/watchlist`
  - Description: Get user's product watchlist
  - Parameters: `userId` (path parameter)
  - Response: Array of watched products

## Request/Response Examples

### Create Baby Product
```json
POST /api/v1/products
{
  "name": "Baby Formula - Organic",
  "brand": "Similac",
  "category": "Feeding",
  "currentPrice": 29.99,
  "description": "Organic baby formula for newborns"
}
```

### Update User Info
```json
PUT /api/v1/users/profile
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@email.com",
  "phoneNumber": "+1234567890"
}
```

### Add Baby
```json
POST /api/v1/users/babies
{
  "name": "Emma",
  "birthDate": "2025-01-15",
  "gender": "female",
  "userId": 123
}
```

## HTTP Status Codes
- **200 OK**: Successful GET, PUT requests
- **201 Created**: Successful POST requests
- **204 No Content**: Successful DELETE requests
- **400 Bad Request**: Invalid request data
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

## Authentication
All endpoints require valid JWT token in Authorization header:
```
Authorization: Bearer <your-jwt-token>
```


## Data Objects Examples

### User Object
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@email.com",
  "phoneNumber": "+1234567890",
  "createdAt": "2025-01-15T10:30:00Z",
  "updatedAt": "2025-01-20T14:45:00Z",
  "isActive": true,
  "preferences": {
    "id": 1,
    "currency": "USD",
    "timezone": "America/Edmonton",
    "emailNotificationsEnabled": true,
    "smsNotificationsEnabled": false
  }
}
```

### Baby Object
```json
{
  "id": 1,
  "name": "Emma",
  "birthDate": "2025-01-15",
  "gender": "female",
  "userId": 123,
  "createdAt": "2025-01-15T10:30:00Z",
  "updatedAt": "2025-01-20T14:45:00Z",
  "details": {
    "weight": "3.5kg",
    "height": "50cm",
    "allergies": ["milk", "nuts"],
    "notes": "Prefers organic products"
  }
}
```

### BabyProduct Object
```json
{
  "id": 1,
  "name": "Baby Formula - Organic",
  "brand": "Similac",
  "category": "Feeding",
  "description": "Organic baby formula for newborns",
  "currency": "CAD",
  "inStock": true,
  "createdAt": "2025-01-15T10:30:00Z",
  "updatedAt": "2025-01-20T14:45:00Z",
  "priceHistory": [
    {
      "retailer": "Amazon",
      "productUrl": "https://amazon.ca/product/123",
      "price": 27.99,
      "date": "2025-01-10T00:00:00Z"
    },
    {
      "retailer": "Walmart",
      "productUrl": "https://walmart.ca/product/456",
      "price": 29.99,
      "date": "2025-01-15T00:00:00Z"
    }
  ]
}
```

### Price Prediction Object
```json
{
  "id": 1,
  "babyProduct": {
    "id": 1,
    "name": "Organic Baby Formula - Infant",
    "brand": "Similac",
    "category": "Feeding"
  },
  "predictedPrice": 29.99,
  "predictionDate": "2025-10-15T08:00:00Z",
  "confidence": 0.92,
  "trend": "decreasing",
  "factors": [
    "Amazon price drop",
    "Seasonal discount",
    "Competitor pricing"
  ],
  "createdAt": "2025-10-12T08:23:19Z"
}
```

### Reminder Object
```json
{
  "id": 1,
  "baby": {
    "id": 1,
    "name": "Emma",
    "dob": "2023-04-15T00:00:00Z"
  },
  "taskTemplate": {
    "id": 5,
    "name": "Feed Baby",
    "description": "Feed baby every 3 hours"
  },
  "vaccination": {
    "id": 2,
    "name": "DTaP",
    "ageMonths": 2
  },
  "milestone": {
    "id": 3,
    "description": "First smile",
    "ageWeeks": 6
  },
  "category": "TASK",
  "dueDate": "2025-10-15",
  "completed": false,
  "completedAt": null
}
```

### Baby Task Template Object Example
```json
{
  "id": 1,
  "title": "Feed Baby",
  "description": "Feed the baby every 3 hours with formula or breast milk",
  "frequency": "DAILY",
  "recurrenceDetail": null,
  "category": "Feeding",
  "intervalDays": null
}
```

### Baby Vaccination Object Example
```json
{
  "id": 1,
  "ageInMonths": 2,
  "vaccines": "DTaP, IPV, Hib",
  "checkUpRequired": true
}
```

### Developmental Milestone Object Example
```json
{
  "id": 1,
  "leapName": "First Smile",
  "occurrenceWeeks": 6,
  "description": "Baby begins to smile socially, responding to familiar faces.",
  "commonFussyBehaviors": "Crying more than usual, clinginess, disrupted sleep patterns"
}
```

### Price History Object Example
```json
{
  "id": 1,
  "retailer": "Amazon",
  "productUrl": "https://amazon.ca/similac-organic-123",
  "price": 32.99,
  "date": "2025-08-13T08:23:19"
}
```

### User Preferences Object
```json
{
  "id": 1,
  "currency": "USD",
  "timezone": "America/Edmonton",
  "emailNotificationsEnabled": true,
  "smsNotificationsEnabled": false
}
```

## Tables
```sql
CREATE TABLE baby_product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255),
    category VARCHAR(100),
    description TEXT,
    currency VARCHAR(10) DEFAULT 'CAD',
    in_stock BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### price_history table (One-To-Many Relationship)
```sql
CREATE TABLE price_history (
    id BIGSERIAL PRIMARY KEY,
    baby_product_id BIGINT NOT NULL,
    retailer VARCHAR(255) NOT NULL,
    product_url TEXT,
    price DECIMAL(10, 2) NOT NULL,
    date TIMESTAMP NOT NULL,
    FOREIGN KEY (baby_product_id) REFERENCES baby_product(id) ON DELETE CASCADE
);

CREATE INDEX idx_price_history_product_id ON price_history(baby_product_id);
CREATE INDEX idx_price_history_date ON price_history(date);
```
