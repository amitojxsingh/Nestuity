// Backend API response types
export interface PriceHistoryItem {
  retailer: string;        // e.g., "Amazon", "Walmart", "Costco"
  productUrl: string;
  price: number;
  date: string;
}

export interface BabyProduct {
  id: number;
  name: string;
  brand: string;
  category: string;
  description: string;
  currency: string;
  inStock: boolean;
  createdAt: string;
  updatedAt: string;
  priceHistory: PriceHistoryItem[];
}

// Frontend display types
export interface PriceData {
  date: string;
  [retailer: string]: number | string;
}

export interface PriceSummary {
  current: number;
  lowest: number;
  highest: number;
  average: number;
  url?: string;
}

export interface VendorData {
  name: string;
  summary: PriceSummary;
  isGoodDeal?: boolean;
}

export interface TimeRange {
  label: string;
  value: string;
  days: number;
}
