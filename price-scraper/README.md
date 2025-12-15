# Price Scraper

Automated price scraping system for baby products from online retailers (Amazon, Walmart, etc.).

### Setup - Configure Environment Variables

Set the following environment variables:

```bash
export SCRAPER_API_KEY="your-scraperapi-key"
export BACKEND_API_URL="http://localhost:8080"  # or nestuity-service:8080 in Docker
export LOG_LEVEL="INFO"  # DEBUG, INFO, WARNING, ERROR
```

## Configuration

### Adding URLs

Add URLs to `test_urls.json` grouped by retailer (Amazon and Walmart):

```json
{
  "retailers": {
    "amazon": [
      "https://www.amazon.ca/...",
      "https://www.amazon.ca/..."
    ],
    "walmart": [
      "https://www.walmart.ca/..."
    ]
  }
}
```

Then run the scraper from the `price-scraper` folder:

```bash
python3 scraper.py
```

The script will read `test_urls.json`, scrape each URL with the appropriate retailer scraper, and print a summary.
### Backend API

The scraper sends data to:
- Endpoint: `POST /api/price-updates`
- Payload:
  ```json
  {
    "productUrl": "https://amazon.ca/...",
    "retailer": "Amazon",
    "price": "29.99",
    "productName": "Pampers Diapers Size 5",
    "brand": "Pampers",
    "category": "Diapering",
    "description": "...",
    "inStock": true,
    "currency": "CAD",
    "scrapedAt": "2025-01-25T10:30:00"
  }
  ```

## Scheduled Execution

The scraper is designed to be triggered by the Spring Boot backend scheduler every 2 weeks via:

```java
@Scheduled(cron = "0 0 0 */15 * ?")  // Every 15 days at midnight
```

## Development

### Adding New Retailers

To support additional retailers beyond Amazon:

1. Create a new scraper class in `scraper.py` (e.g., `WalmartScraper`)
2. Implement the `scrape_product()` method
3. Update `test_urls.json` with test URLs
4. Update `extract_retailer_from_url()` in `utils.py`
5. Update `production_urls.json`

### Rate Limiting

- Increase `SLEEP_BETWEEN_REQUESTS` in `config.py`

## Logging

Logs include:
- Scrape attempts and results
- Backend API calls
- Errors with stack traces (when `LOG_LEVEL=DEBUG`)

Example output:
```
2025-01-25 10:30:00 - scraper - INFO - Scraping: https://amazon.ca/...
2025-01-25 10:30:05 - scraper - INFO - Successfully scraped: Pampers Diapers - $29.99
2025-01-25 10:30:06 - scraper - INFO - Successfully sent price update for Pampers Diapers
```

## Future Enhancements

- [ ] Support for Walmart, Costco, Target scrapers
- [ ] Alerts for price drops
- [ ] Retry logic for failed scrapes
- [ ] Alert system for failed jobs
