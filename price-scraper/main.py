#!/usr/bin/env python3
"""
Main entry point for the price scraper.

This script loads URLs from test_urls.json, scrapes them,
and sends the results to the backend API.
"""
import json
import logging
import os
import sys
from pathlib import Path

from scraper import scrape_batch, send_to_backend
import config

# Configure logging
logging.basicConfig(
    level=getattr(logging, config.LOG_LEVEL),
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


def load_urls_from_json(json_path: str = "test_urls.json") -> list:
    """
    Load URLs from a JSON file grouped by retailer.

    Args:
        json_path: Path to the JSON file

    Returns:
        List of URLs (flattened from all retailers)
    """
    try:
        with open(json_path, 'r') as f:
            data = json.load(f)

            # Flatten all URLs from all retailers
            all_urls = []
            retailers = data.get('retailers', {})
            for retailer, urls in retailers.items():
                all_urls.extend(urls)

            return all_urls

    except FileNotFoundError:
        logger.error(f"File not found: {json_path}")
        return []
    except json.JSONDecodeError as e:
        logger.error(f"Invalid JSON in {json_path}: {e}")
        return []
    except Exception as e:
        logger.error(f"Error loading URLs: {e}", exc_info=True)
        return []


def main():
    """Main execution function."""
    logger.info("=" * 60)
    logger.info("Starting Price Scraper")
    logger.info("=" * 60)

    # Load URLs (use URLS_FILE env var or default to test_urls.json)
    urls_file = os.getenv("URLS_FILE", "test_urls.json")
    logger.info(f"Loading URLs from: {urls_file}")
    urls = load_urls_from_json(urls_file)

    if not urls:
        logger.error("No URLs to scrape. Exiting.")
        sys.exit(1)

    logger.info(f"Loaded {len(urls)} URLs to scrape")

    # Scrape all URLs
    results = scrape_batch(urls)

    # Process results and send to backend
    successful_scrapes = 0
    failed_scrapes = 0
    successful_updates = 0
    failed_updates = 0

    for result in results:
        if result.success:
            successful_scrapes += 1

            # Send to backend
            if send_to_backend(result.product):
                successful_updates += 1
            else:
                failed_updates += 1
        else:
            failed_scrapes += 1
            logger.error(f"Scrape failed: {result.error_message}")

    # Print summary
    logger.info("=" * 60)
    logger.info("Scraping Summary")
    logger.info("=" * 60)
    logger.info(f"Total URLs: {len(urls)}")
    logger.info(f"Successful scrapes: {successful_scrapes}")
    logger.info(f"Failed scrapes: {failed_scrapes}")
    logger.info(f"Successful backend updates: {successful_updates}")
    logger.info(f"Failed backend updates: {failed_updates}")
    logger.info("=" * 60)

    # Exit with appropriate code
    if failed_scrapes > 0 or failed_updates > 0:
        sys.exit(1)
    else:
        sys.exit(0)


if __name__ == "__main__":
    main()
