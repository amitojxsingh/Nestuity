# Email Test API Documentation

## Overview

The `EmailTestController` provides endpoints to manually test email functionality.
**Note:** This controller is **not used in production**. It allows sending sample emails for welcome messages, diaper reminders, and weekly summaries.

---

## Endpoints

---

### 1. Send Welcome Email

**GET** `/email/welcome`

**Description:** Sends a welcome email to a given recipient.

**Query Parameters**

| Name     | Type   | Required | Description                        |
| -------- | ------ | -------- | ---------------------------------- |
| to       | String | Yes      | Recipient email address            |
| username | String | No       | Recipient name (default: "Parent") |

**Response:** Plain text message indicating success or error.

**Example Success Response:**

```
Welcome email sent successfully to user@example.com
```

---

### 2. Send Diaper Reminder Email

**GET** `/email/reminder`

**Description:** Sends a reminder email about remaining diaper supply.

**Query Parameters**

| Name     | Type    | Required | Description                        |
| -------- | ------- | -------- | ---------------------------------- |
| to       | String  | Yes      | Recipient email address            |
| username | String  | No       | Recipient name (default: "Parent") |
| daysLeft | Integer | Yes      | Number of days of diapers left     |

**Response:** Plain text message indicating success or error.

**Example Success Response:**

```
Diaper reminder email sent successfully to user@example.com
```

---

### 3. Send Weekly Summary Email

**GET** `/email/weekly-summary`

**Description:** Sends a weekly summary email including overdue tasks and vaccinations.

**Query Parameters**

| Name     | Type   | Required | Description                        |
| -------- | ------ | -------- | ---------------------------------- |
| to       | String | Yes      | Recipient email address            |
| username | String | No       | Recipient name (default: "Parent") |

**Response:** Plain text message indicating success or error.

**Example Success Response:**

```
Weekly summary email sent successfully to user@example.com
```

**Note:** The endpoint uses dummy data for overdue tasks and vaccinations when testing.
