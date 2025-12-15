## **BabyController — API Documentation**

## **Overview**

The `BabyController` handles all operations related to baby profiles and diaper usage in the Nestuity service.
It provides endpoints for:

* Creating baby profiles
* Retrieving baby information (all babies, by user, by ID)
* Updating and deleting baby profiles
* Calculating and updating diaper usage for each baby

The controller also integrates with the diaper usage calculator and reminder services to ensure users receive accurate diaper consumption estimates and automated reminders.

---

# # **API Endpoints Summary Table**

| Method | Endpoint                            | Description                                 |
| ------ | ----------------------------------- | ------------------------------------------- |
| POST   | `/api/babies`                       | Create a new baby profile                   |
| GET    | `/api/babies`                       | Get all babies                              |
| GET    | `/api/babies/user/{userId}`         | Get all babies belonging to a specific user |
| GET    | `/api/babies/{id}`                  | Get a baby by ID                            |
| PUT    | `/api/babies/{id}`                  | Update a baby profile                       |
| DELETE | `/api/babies/{id}`                  | Delete a baby by ID                         |
| GET    | `/api/babies/{babyId}/diaper-usage` | Get diaper usage stats for a baby           |
| PUT    | `/api/babies/{babyId}/diaper-usage` | Update diaper usage stats                   |

---

# # **Detailed Endpoint Documentation**

---

## ## **1. Create Baby**

### **POST** `/api/babies`

### **Description**

Creates a new baby profile for a parent user.
Daily diaper usage is auto-calculated based on weight using internal usage norms.

### **Request Parameters**

| Name       | Type   | In   | Required | Description                  |
| ---------- | ------ | ---- | -------- | ---------------------------- |
| userId     | Long   | body | ✔        | Parent user ID               |
| name       | String | body | ✔        | Baby's name                  |
| weight     | Double | body | ✔        | Baby's weight in kilograms   |
| dob        | String | body | ✔        | Date of birth (YYYY-MM-DD)   |
| diaperSize | String | body | ✖        | Defaults to `"1"` if missing |

### **Response**

| Status | Meaning        | Body           |
| ------ | -------------- | -------------- |
| 200    | Baby created   | `BabyResponse` |
| 400    | Invalid data   | Error message  |
| 404    | User not found | Error message  |

---

## ## **2. Get All Babies**

### **GET** `/api/babies`

### **Description**

Returns a list of all babies in the system.

### **Request Parameters**

*None*

### **Response**

| Status | Body                 |
| ------ | -------------------- |
| 200    | `List<BabyResponse>` |

---

## ## **3. Get Babies by User**

### **GET** `/api/babies/user/{userId}`

### **Description**

Returns all babies associated with a specific parent user.

### **Path Parameters**

| Name   | Type | Required | Description |
| ------ | ---- | -------- | ----------- |
| userId | Long | ✔        | User ID     |

### **Response**

| Status | Body                 |
| ------ | -------------------- |
| 200    | `List<BabyResponse>` |

---

## ## **4. Get Baby by ID**

### **GET** `/api/babies/{id}`

### **Description**

Fetches a single baby's details.

### **Path Parameters**

| Name | Type | Required | Description |
| ---- | ---- | -------- | ----------- |
| id   | Long | ✔        | Baby ID     |

### **Response**

| Status | Body           |
| ------ | -------------- |
| 200    | `BabyResponse` |
| 404    | Not found      |

---

## ## **5. Update Baby**

### **PUT** `/api/babies/{id}`

### **Description**

Updates the baby's profile including name, date of birth, weight, diaper size, and daily usage.
If dailyUsage is negative, a new one is calculated from weight.

### **Path Parameters**

| Name | Type | Required | Description |
| ---- | ---- | -------- | ----------- |
| id   | Long | ✔        | Baby ID     |

### **Body Parameters**

| Name       | Type    | Required | Description                                        |
| ---------- | ------- | -------- | -------------------------------------------------- |
| name       | String  | ✔        | Baby's updated name                                |
| dob        | Date    | ✔        | Updated birth date                                 |
| weight     | Double  | ✔        | Updated weight                                     |
| diaperSize | String  | ✔        | Updated diaper size                                |
| dailyUsage | Integer | ✔        | New daily usage OR negative to force recalculation |

### **Response**

| Status | Body           |
| ------ | -------------- |
| 200    | `BabyResponse` |
| 404    | Not found      |

---

## ## **6. Delete Baby**

### **DELETE** `/api/babies/{id}`

### **Description**

Deletes a baby profile permanently.

### **Path Parameters**

| Name | Type | Required | Description |
| ---- | ---- | -------- | ----------- |
| id   | Long | ✔        | Baby ID     |

### **Response**

| Status | Body                           |
| ------ | ------------------------------ |
| 200    | `"Baby deleted successfully."` |
| 404    | Not found                      |

---

## ## **7. Get Diaper Usage**

### **GET** `/api/babies/{babyId}/diaper-usage`

### **Description**

Returns calculated diaper usage info (remaining diapers, days left, daily usage).

### **Path Parameters**

| Name   | Type | Required | Description |
| ------ | ---- | -------- | ----------- |
| babyId | Long | ✔        | Baby ID     |

### **Response**

| Status | Body                  |
| ------ | --------------------- |
| 200    | `DiaperUsageResponse` |

---

## ## **8. Update Diaper Usage**

### **PUT** `/api/babies/{babyId}/diaper-usage`

### **Description**

Updates diaper usage based on the number of diapers used.

### **Path Parameters**

| Name   | Type | Required |
| ------ | ---- | -------- |
| babyId | Long | ✔        |

### **Body Parameters**

| Name        | Type    | Required | Description            |
| ----------- | ------- | -------- | ---------------------- |
| diapersUsed | Integer | ✔        | Number of diapers used |

### **Response**

| Status | Body                  |
| ------ | --------------------- |
| 200    | `DiaperUsageResponse` |

