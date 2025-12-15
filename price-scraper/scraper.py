"""
Main scraper module for extracting product prices from online retailers.
"""
import requests
from bs4 import BeautifulSoup
import re
import time
import logging
from typing import List
from decimal import Decimal
from abc import ABC, abstractmethod
from urllib.parse import urlparse

from models import ScrapedProduct, ScrapeResult
from utils import extract_retailer_from_url, parse_price, clean_text, is_valid_url
import config

logging.basicConfig(
    level=getattr(logging, config.LOG_LEVEL),
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


class BaseScraper(ABC):
    """Base class for all product scrapers."""

    def __init__(self, api_key: str = None):
        """
        Initialize the scraper.

        Args:
            api_key: ScraperAPI key (optional, defaults to config)
        """
        self.api_key = api_key or config.SCRAPER_API_KEY
        self.scraper_api_url = config.SCRAPER_API_URL

    @abstractmethod
    def _extract_product_title(self, html: BeautifulSoup) -> str:
        """Extract product title from HTML. Must be implemented by subclasses."""
        pass

    @abstractmethod
    def _extract_price(self, html: BeautifulSoup) -> Decimal:
        """Extract price from HTML. Must be implemented by subclasses."""
        pass

    def scrape_product(self, product_url: str) -> ScrapeResult:
        """
        Scrape a single product page.

        Args:
            product_url: URL of the product

        Returns:
            ScrapeResult containing the scraped product or error information
        """
        if not is_valid_url(product_url):
            logger.error(f"Invalid URL: {product_url}")
            return ScrapeResult.error_result(f"Invalid URL: {product_url}")

        try:
            logger.info(f"Scraping: {product_url}")
            use_proxy = bool(self.api_key) and self.api_key != "API-KEY" and getattr(config, "USE_SCRAPER_API", True)

            if use_proxy:
                payload = {
                    "api_key": self.api_key,
                    "url": product_url,
                    "max_cost": config.MAX_COST
                }
                response = requests.get(self.scraper_api_url, params=payload, timeout=30)
                logger.info(f"ScraperAPI response status: {response.status_code}")
            else:       
                logger.info("Fetching directly without ScraperAPI (USE_SCRAPER_API disabled or missing API key)")   
                response = requests.get(product_url, headers=config.REQUEST_HEADERS, timeout=30)
                logger.info(f"Direct fetch status: {response.status_code}")

            if response.status_code != 200:
                error_msg = f"HTTP {response.status_code} for {product_url}"
                logger.error(error_msg)
                return ScrapeResult.error_result(error_msg, response.status_code)

            html = BeautifulSoup(response.content, "html.parser")       # parse HTML for product information

            product_name = self._extract_product_title(html)
            price = self._extract_price(html)
            description = self._extract_description(html)
            retailer = extract_retailer_from_url(product_url)

            if not product_name:
                error_msg = f"Failed to extract product title from {product_url}"
                logger.warning(error_msg)
                return ScrapeResult.error_result(error_msg)

            if not price:
                error_msg = f"Failed to extract price from {product_url}"
                logger.warning(error_msg)
                return ScrapeResult.error_result(error_msg)
            product = ScrapedProduct(
                product_url=product_url,
                retailer=retailer,
                price=price,
                product_name=product_name,
                description=description,
                in_stock=True,  
                currency="CAD"  
            )

            logger.info(f"Successfully scraped: {product_name} - ${price}")
            return ScrapeResult.success_result(product)

        except requests.RequestException as e:
            error_msg = f"Request failed for {product_url}: {str(e)}"
            logger.error(error_msg)
            return ScrapeResult.error_result(error_msg)
        except Exception as e:
            error_msg = f"Unexpected error scraping {product_url}: {str(e)}"
            logger.error(error_msg, exc_info=True)
            return ScrapeResult.error_result(error_msg)


class AmazonScraper(BaseScraper):
    """Scraper for Amazon product pages."""

    def _extract_product_title(self, html: BeautifulSoup) -> str:
        """Extract product title from Amazon page."""
        title_span = html.find("span", id="productTitle")
        if title_span:
            return clean_text(title_span.text)
        return None

    def _extract_price(self, html: BeautifulSoup) -> Decimal:
        """Extract price from Amazon page."""
        # Try to find price components
        price_whole_span = html.find("span", class_="a-price-whole")
        price_fraction_span = html.find("span", class_="a-price-fraction")

        if price_whole_span and price_fraction_span:
            return parse_price(price_whole_span.text, price_fraction_span.text)

        # Alternative: Try to find complete price
        price_span = html.find("span", class_="a-offscreen")
        if price_span:
            # Extract price from text like "$29.99"
            price_text = clean_text(price_span.text)
            if price_text:
                # Remove currency symbols and parse
                import re
                match = re.search(r'(\d+)\.(\d+)', price_text)
                if match:
                    return parse_price(match.group(1), match.group(2))

        return None

    def _extract_description(self, html: BeautifulSoup) -> str:
        """Extract product description from Amazon page."""
        # Find div with id="productDescription"
        product_desc_div = html.find("div", id="productDescription")

        if product_desc_div:
            # Navigate to p tag, then span inside it
            p_tag = product_desc_div.find("p")
            if p_tag:
                span_tag = p_tag.find("span")
                if span_tag:
                    return clean_text(span_tag.text)
                # Fallback: try getting text from p tag directly
                return clean_text(p_tag.text)

        return None


class WalmartScraper(BaseScraper):
    """Scraper for Walmart product pages."""

    def _extract_product_title(self, html: BeautifulSoup) -> str:
        """Extract product title from Walmart page."""
        try:
            import json
            for script in html.find_all("script", {"type": "application/ld+json"}):
                try:
                    data = json.loads(script.string or "{}")    # load JSON-LD data for parsing
                except Exception:
                    continue

                nodes = data if isinstance(data, list) else [data]  # ensure list for uniform processing
                for node in nodes:
                    if isinstance(node, dict) and node.get("@type") in ("Product", "BreadcrumbList"):
                        name = node.get("name")
                        if name:
                            return clean_text(name)
                        offers = node.get("offers")
        except Exception:
            pass

        selectors = [       # try common title containers of webpages
            ("h1", {"class": "prod-ProductTitle"}),
            ("h1", {"class": "product-title"}),
            ("h1", {"data-automation-id": "product-title"}),
            ("h1", {}),
        ]
        for tag, attrs in selectors:
            element = html.find(tag, attrs=attrs)        # look for title of the product
            if element and element.text:
                return clean_text(element.text)

        meta_title = html.find("meta", {"property": "og:title"})    # fallback to og:title meta tag
        if meta_title and meta_title.get("content"):
            return clean_text(meta_title.get("content"))

        return None

    def _extract_price(self, html: BeautifulSoup) -> Decimal:
        """Extract price from Walmart page.
        Strategy (most reliable first):
        - JSON-LD script offers.price
        - Meta/itemprop price tags
        - Server-rendered price elements/aria-labels
        - Robust fallback regex across page text
        """
        try:
            import json

            def find_price_in_json(obj):
                """Recursively search for a numeric price string in Walmart JSON structures."""
                if isinstance(obj, dict):
                    for key in ("price", "priceAmount", "currentPrice", "displayPrice"):
                        if key in obj and isinstance(obj[key], (str, int, float)):
                            match = re.search(r"\d+(?:[\.,]\d+)?", str(obj[key]))       # look for numeric price pattern
                            if match:
                                return match.group(0).replace(",", ".")
                    offers = obj.get("offers")
                    if isinstance(offers, dict):
                        for key in ("price", "lowPrice", "highPrice"):
                            if key in offers and isinstance(offers[key], (str, int, float)):
                                match = re.search(r"\d+(?:[\.,]\d+)?", str(offers[key]))        # look for numeric price pattern
                                if match:
                                    return match.group(0).replace(",", ".")
                    for value in obj.values():
                        found = find_price_in_json(value)
                        if found:
                            return found
                elif isinstance(obj, list):
                    for item in obj:
                        found = find_price_in_json(item)
                        if found:
                            return found
                return None

            for script in html.find_all("script", {"type": "application/ld+json"}):
                raw = script.string or script.text or ""
                if not raw.strip():
                    continue
                try:
                    data = json.loads(raw)
                except Exception:
                    nums = re.findall(r'"price"\s*:\s*"?(\d+(?:[\.,]\d+)?)', raw)   # fallback regex search for price patterns
                    if nums:
                        return Decimal(nums[0].replace(',', '.'))
                    continue

                price_str = find_price_in_json(data)
                if price_str:
                    price_str = price_str.replace(",", ".") # normalize decimal point
                    try:
                        return Decimal(price_str)
                    except Exception:
                        pass
        except Exception:
            pass
        meta_selectors = [
            ("meta", {"itemprop": "price"}, "content"),
            ("meta", {"property": "product:price:amount"}, "content"),
            ("meta", {"property": "og:price:amount"}, "content"),
        ]
        for tag, attrs, attr_key in meta_selectors:
            element = html.find(tag, attrs=attrs)
            if element and element.get(attr_key):
                match = re.search(r"\d+(?:\.\d+)?", element.get(attr_key))
                if match:
                    return Decimal(match.group(1) if match.groups() else match.group(0))
        selectors = [
            ("span", {"class": "price-characteristic"}),  
            ("span", {"class": "price-group"}),           
            ("div", {"data-automation-id": "price"}),     
            ("span", {"itemprop": "price"}),
            ("div", {"data-testid": "price"}),
        ]
        for tag, attrs in selectors:
            element = html.find(tag, attrs=attrs)
            if not element:
                continue
            aria = element.get("aria-label") or element.get("aria-valuetext")
            if aria:
                match = re.search(r"\d+(?:\.\d+)?", aria)
                if match:
                    return Decimal(match.group(0))

            content_attr = element.get("content")
            if content_attr:
                match = re.search(r"\d+(?:\.\d+)?", content_attr)
                if match:
                    return Decimal(match.group(0))

            text = clean_text(element.get_text())
            if text:
                match = re.search(r"\$\s*(\d+(?:\.\d+)?)", text)
                if match:
                    return Decimal(match.group(1))
                match = re.search(r"\d+(?:\.\d+)?", text)
                if match:
                    return Decimal(match.group(0))
        all_text = html.get_text(" ", strip=True)
        match = re.search(r"\$\s*(\d+(?:\.\d+)?)", all_text)
        if match:
            return Decimal(match.group(1))

        return None

    def _extract_description(self, html: BeautifulSoup) -> str:
        """Extract product description from Walmart page."""
        selectors = [
            ("div", {"id": "product-description"}),
            ("div", {"class": "about-desc"}),
            ("div", {"class": "ProductDescription-content"}),
            ("div", {"data-testid": "product-description"}),
        ]
        for tag, attrs in selectors:
            element = html.find(tag, attrs=attrs)
            if element:
                return clean_text(element.get_text())

        meta_desc = html.find("meta", {"name": "description"})
        if meta_desc and meta_desc.get("content"):
            return clean_text(meta_desc["content"])

        return None


def get_scraper_for_url(url: str) -> BaseScraper:
    """
    Factory function to get the appropriate scraper for a URL.

    Args:
        url: Product URL

    Returns:
        Appropriate scraper instance

    Raises:
        ValueError: If no scraper is available for the URL
    """
    parsed = urlparse(url)
    domain = parsed.netloc.lower()

    if 'amazon' in domain:
        return AmazonScraper()
    elif 'walmart' in domain:
        return WalmartScraper()
    else:
        raise ValueError(f"No scraper available for domain: {domain}")


def scrape_batch(urls: List[str], sleep_time: int = None) -> List[ScrapeResult]:
    """
    Scrape multiple product URLs using appropriate scrapers.

    Args:
        urls: List of product URLs to scrape
        sleep_time: Time to sleep between requests (optional)

    Returns:
        List of ScrapeResult objects
    """
    results = []
    sleep_time = sleep_time or config.SLEEP_BETWEEN_REQUESTS

    logger.info(f"Starting batch scrape of {len(urls)} URLs")

    for i, url in enumerate(urls):
        try:
            scraper = get_scraper_for_url(url)
            result = scraper.scrape_product(url)
        except ValueError as e:
            logger.error(str(e))
            result = ScrapeResult.error_result(str(e))
        except NotImplementedError as e:
            logger.error(f"Scraper not implemented for {url}: {str(e)}")
            result = ScrapeResult.error_result(str(e))

        results.append(result)

        if i < len(urls) - 1: 
            logger.info(f"Sleeping for {sleep_time} seconds...")
            time.sleep(sleep_time)

    successful = sum(1 for r in results if r.success)
    failed = len(results) - successful
    logger.info(f"Batch scrape complete: {successful} successful, {failed} failed")

    return results


def send_to_backend(product: ScrapedProduct) -> bool:
    """
    Send scraped product data to the backend API.

    Args:
        product: ScrapedProduct to send

    Returns:
        True if successful, False otherwise
    """
    try:
        payload = product.to_api_payload()
        logger.info(f"Sending to backend: {config.PRICE_UPDATE_ENDPOINT}")
        logger.debug(f"Payload: {payload}")

        response = requests.post(
            config.PRICE_UPDATE_ENDPOINT,
            json=payload,
            headers={"Content-Type": "application/json"},
            timeout=10
        )

        if response.status_code in [200, 201]:
            logger.info(f"Successfully sent price update for {product.product_name}")
            return True
        else:
            logger.error(f"Backend returned {response.status_code}: {response.text}")
            return False

    except requests.RequestException as e:
        logger.error(f"Failed to send data to backend: {str(e)}")
        return False
    except Exception as e:
        logger.error(f"Unexpected error sending to backend: {str(e)}", exc_info=True)
        return False
    
if __name__ == "__main__":
    """Load URLs from test_urls.json and run a batch scrape."""
    import os
    import json

    here = os.path.dirname(os.path.abspath(__file__))
    urls_path = os.path.join(here, "test_urls.json")

    if not os.path.exists(urls_path):
        print(f"Could not find test_urls.json at: {urls_path}")
        raise SystemExit(1)
    try:
        with open(urls_path, "r", encoding="utf-8") as f:
            data = json.load(f)
    except Exception as e:
        print(f"Failed to read test_urls.json: {e}")
        raise SystemExit(1)

    retailers = (data or {}).get("retailers", {})
    amazon_urls = retailers.get("amazon", []) or []
    walmart_urls = retailers.get("walmart", []) or []

    urls = [*amazon_urls, *walmart_urls]

    if not urls:
        print("No URLs found in test_urls.json. Add some under retailers.amazon or retailers.walmart.")
        raise SystemExit(1)

    print(f"Found {len(urls)} URLs in test_urls.json. Starting batch scrape...\n")

    results = scrape_batch(urls)