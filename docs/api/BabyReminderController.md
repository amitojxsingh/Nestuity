# Baby Reminder API Documentation

This API manages reminders associated with a baby, including milestone reminders, medical reminders, custom task reminders, and upcoming or overdue reminders.

---

## Base URL

```
/api/reminders
```

---

# Endpoints

---

## 1. Create Baseline Reminders for a Baby

**POST** `/api/reminders/{babyId}`

### Description

Creates a predefined set of baseline reminders for the specified baby.
These reminders are loaded from JSON and attached to the baby.

### Path Variables

| Name   | Type | Description                           |
| ------ | ---- | ------------------------------------- |
| babyId | Long | ID of the baby to attach reminders to |

### Response

`200 OK`
No body.

---

## 2. Get Reminder by ID

**GET** `/api/reminders/{id}`

### Description

Retrieves a specific reminder by its ID.

### Path Variables

| Name | Type | Description        |
| ---- | ---- | ------------------ |
| id   | Long | ID of the reminder |

### Response

`200 OK` → `BabyReminderDto`
`404 Not Found` if not found.

---

## 3. Get All Reminders for a Baby

**GET** `/api/reminders/baby/{babyId}`

### Description

Returns all reminders associated with a baby, including computed fields such as `nextDue`.

### Path Variables

| Name   | Type | Description    |
| ------ | ---- | -------------- |
| babyId | Long | ID of the baby |

### Response

`200 OK` → `List<BabyReminderDto>`

---

## 4. Get Upcoming Reminders

**GET** `/api/reminders/baby/{babyId}/upcoming`

### Description

Returns upcoming reminders for the baby.
The user can control how far ahead to search using the optional `daysAhead` parameter.

### Path Variables

| Name   | Type | Description    |
| ------ | ---- | -------------- |
| babyId | Long | ID of the baby |

### Query Parameters

| Name      | Type    | Required | Description                                                       |
| --------- | ------- | -------- | ----------------------------------------------------------------- |
| daysAhead | Integer | No       | Number of days in the future to check (default chosen by service) |

### Response

`200 OK` → `List<BabyReminderDto>`

---

## 5. Get Reminders by Type

**GET** `/api/reminders/baby/{babyId}/reminders`

### Description

Returns reminders filtered by category.
Available types:

* `medical`
* `overdue`

### Path Variables

| Name   | Type | Description    |
| ------ | ---- | -------------- |
| babyId | Long | ID of the baby |

### Query Parameters

| Name | Type   | Required | Description                   |
| ---- | ------ | -------- | ----------------------------- |
| type | String | Yes      | Either `medical` or `overdue` |

### Response

`200 OK` → `List<BabyReminderDto>`
`400 Bad Request` for invalid type.

---

## 6. Create Custom Task Reminder

**POST** `/api/reminders/baby/{babyId}/task`

### Description

Creates a custom (task) reminder for a baby.
User-generated tasks only.

### Path Variables

| Name   | Type | Description    |
| ------ | ---- | -------------- |
| babyId | Long | ID of the baby |

### Request Body

`BabyReminderDto`

### Response

`200 OK` → Newly created `BabyReminderDto`

---

## 7. Update Reminder

**PUT** `/api/reminders/{id}`

### Description

Updates an existing reminder. Partial updates are supported through the DTO.

### Path Variables

| Name | Type | Description |
| ---- | ---- | ----------- |
| id   | Long | Reminder ID |

### Request Body

`BabyReminderDto`

### Response

`200 OK` → Updated `BabyReminderDto`

---

## 8. Mark Reminder as Completed

**PUT** `/api/reminders/{id}/complete`

### Description

Marks a reminder as completed (normal completion).

### Path Variables

| Name | Type | Description |
| ---- | ---- | ----------- |
| id   | Long | Reminder ID |

### Response

`200 OK`
No body.

---

## 9. Complete Task Reminder (Task-Only Completion)

**DELETE** `/api/reminders/{id}/task-complete`

### Description

Marks a TASK reminder as completed.
This is not a permanent delete; it simply marks completion.

### Path Variables

| Name | Type | Description      |
| ---- | ---- | ---------------- |
| id   | Long | Task reminder ID |

### Response

`200 OK`
No body.

---

## 10. Delete Reminder (Permanent Delete)

**DELETE** `/api/reminders/{id}`

### Description

Permanently deletes a reminder. Only allowed for TASK-type reminders.

### Path Variables

| Name | Type | Description        |
| ---- | ---- | ------------------ |
| id   | Long | ID of the reminder |

### Response

`204 No Content`

---

## 11. Get Current Milestone Reminder

**GET** `/api/reminders/baby/{babyId}/current`

### Description

Returns the baby's current milestone reminder.
If no milestone is currently active, returns an empty DTO.

### Path Variables

| Name   | Type | Description    |
| ------ | ---- | -------------- |
| babyId | Long | ID of the baby |

### Response

`200 OK` → `BabyReminderDto`

