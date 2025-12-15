"""
Data models for scraped product information.
"""
from dataclasses import dataclass
from decimal import Decimal
from datetime import datetime
from typing import Optional


@dataclass
class ScrapedProduct:
    """Represents a product scraped from an online retailer."""

    product_url: str
    retailer: str
    price: Decimal
    product_name: Optional[str] = None
    brand: Optional[str] = None
    category: Optional[str] = None
    description: Optional[str] = None
    in_stock: bool = True
    currency: str = "CAD"
    scraped_at: datetime = None

    def __post_init__(self):
        if self.scraped_at is None:
            self.scraped_at = datetime.now()

    def to_api_payload(self) -> dict:
        """Convert to JSON payload for backend API."""
        return {
            "productUrl": self.product_url,
            "retailer": self.retailer,
            "price": str(self.price),
            "productName": self.product_name,
            "brand": self.brand,
            "category": self.category,
            "description": self.description,
            "inStock": self.in_stock,
            "currency": self.currency,
            "scrapedAt": self.scraped_at.isoformat()
        }


@dataclass
class ScrapeResult:
    """Result of a scraping operation."""

    success: bool
    product: Optional[ScrapedProduct] = None
    error_message: Optional[str] = None
    status_code: Optional[int] = None

    @classmethod
    def success_result(cls, product: ScrapedProduct):
        return cls(success=True, product=product)

    @classmethod
    def error_result(cls, error_message: str, status_code: Optional[int] = None):
        return cls(success=False, error_message=error_message, status_code=status_code)
