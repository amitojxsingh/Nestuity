"""
Configuration for the price scraper system.
"""
import os
from pathlib import Path

# Load environment variables from .env files (project root and current dir)
try:
    from dotenv import load_dotenv  # type: ignore

    here = Path(__file__).resolve()
    candidates = [
        here.parent / ".env",           # price-scraper/.env
        here.parents[1] / ".env",       # repo root .env
    ]
    for env_path in candidates:
        if env_path.exists():
            load_dotenv(env_path)
    # Also load from current working directory as a fallback
    load_dotenv()
except Exception:
    # If python-dotenv is unavailable or loading fails, proceed with OS env only
    pass

# ScraperAPI Configuration
SCRAPER_API_KEY = os.getenv("SCRAPER_API_KEY", "API-KEY")
SCRAPER_API_URL = "https://api.scraperapi.com/"
MAX_COST = "5"
USE_SCRAPER_API = os.getenv("USE_SCRAPER_API", "true").lower() == "true"

# Backend API Configuration
BACKEND_API_URL = os.getenv("BACKEND_API_URL", "http://nestuity-service:8080")
PRICE_UPDATE_ENDPOINT = f"{BACKEND_API_URL}/api/price-updates"

# Scraper Settings
REQUEST_HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36",
    "Accept-Language": "en-US,en;q=0.9",
    "Accept-Encoding": "gzip, deflate, br",
    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
    "Connection": "keep-alive",
    "Upgrade-Insecure-Requests": "1",
}

# Rate Limiting
SLEEP_BETWEEN_REQUESTS = 10  # seconds

# Logging
LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO")
