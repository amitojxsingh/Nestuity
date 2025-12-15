"""
Utility functions for the price scraper.
"""
import re
from decimal import Decimal
from typing import Optional
from urllib.parse import urlparse


def extract_retailer_from_url(url: str) -> str:
    """
    Extract the retailer name from a product URL.

    Args:
        url: Product URL (e.g., https://www.amazon.ca/product/...)

    Returns:
        Retailer name (e.g., "Amazon")
    """
    parsed = urlparse(url)
    domain = parsed.netloc.lower()

    if 'amazon' in domain:
        return 'Amazon'
    elif 'walmart' in domain:
        return 'Walmart'
    else:
        # Extract domain name as fallback
        domain_parts = domain.replace('www.', '').split('.')
        return domain_parts[0].capitalize()


def parse_price(price_whole: Optional[str], price_fraction: Optional[str]) -> Optional[Decimal]:
    """
    Parse price components into a Decimal value.

    Args:
        price_whole: Whole dollar amount (e.g., "29")
        price_fraction: Fractional amount (e.g., "99")

    Returns:
        Decimal price or None if parsing fails
    """
    try:
        if not price_whole:
            return None

        # Clean the whole price (remove commas, dollar signs, etc.)
        cleaned_whole = re.sub(r'[^\d]', '', price_whole)

        if price_fraction:
            # Clean the fraction
            cleaned_fraction = re.sub(r'[^\d]', '', price_fraction)
            price_str = f"{cleaned_whole}.{cleaned_fraction}"
        else:
            price_str = cleaned_whole

        return Decimal(price_str)
    except (ValueError, TypeError):
        return None


def clean_text(text: Optional[str]) -> Optional[str]:
    """
    Clean and normalize text from HTML.

    Args:
        text: Raw text from HTML

    Returns:
        Cleaned text or None
    """
    if not text:
        return None

    # Strip whitespace and newlines
    cleaned = text.strip()

    # Remove multiple spaces
    cleaned = re.sub(r'\s+', ' ', cleaned)

    return cleaned if cleaned else None


def is_valid_url(url: str) -> bool:
    """
    Validate if a string is a valid URL.

    Args:
        url: URL to validate

    Returns:
        True if valid, False otherwise
    """
    try:
        result = urlparse(url)
        return all([result.scheme, result.netloc])
    except ValueError:
        return False
