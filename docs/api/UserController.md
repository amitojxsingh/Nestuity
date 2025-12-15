# User API Documentation

## Overview

The User API handles CRUD operations for users, including authentication and user preferences management.
It supports creating, updating, deleting users, logging in, and managing user-specific preferences.

---

# Endpoints

---

## 1. Create User

**POST** `/api/users`

### Description

Creates a new user. Supports both OAuth (e.g., Google) and credentials-based signup.

### Request Body (`SaveUserRequest`)

| Field        | Type   | Description                     |
| ------------ | ------ | ------------------------------- |
| email        | String | User email                      |
| firstName    | String | User first name                 |
| lastName     | String | User last name                  |
| phoneNumber  | String | Optional phone number           |
| password     | String | Required for credentials signup |
| authProvider | String | "credentials" or "google"       |
| providerId   | String | Required for OAuth signup       |

### Response Body (`UserResponse`)

| Field       | Type            | Description             |
| ----------- | --------------- | ----------------------- |
| id          | Long            | User ID                 |
| email       | String          | User email              |
| firstName   | String          | First name              |
| lastName    | String          | Last name               |
| phoneNumber | String          | Phone number (nullable) |
| active      | Boolean         | Active status           |
| createdAt   | DateTime        | Creation timestamp      |
| updatedAt   | DateTime        | Last update timestamp   |
| preferences | UserPreferences | Optional preferences    |

### Response Codes

| Status              | Meaning                        |
| ------------------- | ------------------------------ |
| **200 OK**          | User created successfully      |
| **409 CONFLICT**    | User with email already exists |
| **400 BAD REQUEST** | Invalid request body           |

---

## 2. Login

**POST** `/api/users/login`

### Description

Authenticate a user with email and password.

### Request Body (`LoginRequest`)

| Field    | Type   | Description   |
| -------- | ------ | ------------- |
| email    | String | User email    |
| password | String | User password |

### Response Body (`UserResponse`)

Same as **Create User** response.

### Response Codes

| Status               | Meaning                   |
| -------------------- | ------------------------- |
| **200 OK**           | Successful login          |
| **401 UNAUTHORIZED** | Invalid email or password |

---

## 3. Get All Users

**GET** `/api/users`

### Description

Retrieve all users in the system.

### Response Body

List of `User` entities.

---

## 4. Get User By ID

**GET** `/api/users/{id}`

### Response Body

`User` entity

### Response Codes

| Status            | Meaning        |
| ----------------- | -------------- |
| **200 OK**        | User found     |
| **404 NOT FOUND** | User not found |

---

## 5. Update User

**PUT** `/api/users/{id}`

### Request Body (`SaveUserRequest`)

Partial or full update for user fields.

### Response Body (`UserResponse`)

Updated user object.

### Response Codes

| Status            | Meaning                   |
| ----------------- | ------------------------- |
| **200 OK**        | User updated successfully |
| **404 NOT FOUND** | User not found            |

---

## 6. Delete User

**DELETE** `/api/users/{id}`

### Response Codes

| Status             | Meaning        |
| ------------------ | -------------- |
| **204 NO CONTENT** | User deleted   |
| **404 NOT FOUND**  | User not found |

---

## 7. Get User Preferences

**GET** `/api/users/{id}/preferences`

### Response Body (`UserPreferences`)

| Field                     | Type | Description               |
| ------------------------- | ---- | ------------------------- |
| Various preference fields | …    | Depends on implementation |

### Response Codes

| Status            | Meaning               |
| ----------------- | --------------------- |
| **200 OK**        | Preferences found     |
| **404 NOT FOUND** | Preferences not found |

---

## 8. Update or Create User Preferences

**PUT** `/api/users/{id}/preferences`

### Request Body (`UserPreferences`)

Fields for user preferences.

### Response Body (`UserPreferences`)

Updated preferences.

### Response Codes

| Status            | Meaning             |
| ----------------- | ------------------- |
| **200 OK**        | Preferences updated |
| **404 NOT FOUND** | User not found      |

---

## 9. Delete User Preferences

**DELETE** `/api/users/{id}/preferences`

### Response Codes

| Status             | Meaning             |
| ------------------ | ------------------- |
| **204 NO CONTENT** | Preferences deleted |
