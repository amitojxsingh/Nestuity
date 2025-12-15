# Price Scraper Admin API Documentation

## Overview

The Price Scraper Admin API provides administrative control over the price-scraping scheduler.
It allows administrators to manually trigger the scraper, view its operational status, and enable or disable scheduled execution.

This API is intended **only for admin/internal use** and should be protected by authentication and/or network rules.

---

# Endpoints

---

## 1. Trigger Scraper Manually

**POST** `/api/admin/scraper/trigger`

### Description

Immediately triggers the price scraper without waiting for the scheduled Cron job.
Useful for debugging and manual runs.

### Request Body

None.

### Response (200 OK)

```json
{
  "success": true,
  "message": "Price scraper triggered successfully"
}
```

### Response (500 Internal Server Error)

```json
{
  "success": false,
  "message": "Failed to trigger scraper: <error>"
}
```

---

## 2. Get Scraper Status

**GET** `/api/admin/scraper/status`

### Description

Returns the current configuration and environment details of the price scraper.

### Response Fields

| Field      | Type    | Description                                                                      |
| ---------- | ------- | -------------------------------------------------------------------------------- |
| enabled    | boolean | Whether the scraper is enabled via application configuration (`scraper.enabled`) |
| schedule   | String  | Cron schedule for automatic scraping                                             |
| pythonPath | String  | Path to the Python interpreter                                                   |
| scriptPath | String  | Path to the Python scraping script                                               |

### Example Response

```json
{
  "enabled": true,
  "schedule": "0 0 0 1,15 * ? (1st and 15th of each month at midnight)",
  "pythonPath": "/usr/bin/python3",
  "scriptPath": "/opt/scraper/scrape.py"
}
```

---

## 3. Enable or Disable the Scraper

**POST** `/api/admin/scraper/toggle?enabled={value}`

### Description

Allows the admin to set whether the scheduler is enabled or disabled.
Note: Changing `scraper.enabled` requires an application restart to apply.

### Query Parameters

| Name    | Type    | Required | Description                          |
| ------- | ------- | -------- | ------------------------------------ |
| enabled | boolean | Yes      | `true` to enable, `false` to disable |

### Example Request

```
POST /api/admin/scraper/toggle?enabled=false
```

### Response (200 OK)

```json
{
  "success": true,
  "enabled": false,
  "message": "Scraper disabled",
  "note": "Changes to scraper.enabled require application restart to take effect"
}
```
