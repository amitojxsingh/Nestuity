import { BabyProduct, PriceData, PriceHistoryItem, PriceSummary, VendorData } from '@/types/product.types';

/**
 * Transform price history from backend format to chart-friendly format
 * Groups prices by date with dynamic vendor columns
 */
export const transformPriceHistory = (product: BabyProduct) => {
  // Create a map to group prices by date
  const priceDataMap = new Map<string, PriceData>();
  const vendors = new Set<string>();
  const vendorUrls = new Map<string, string>(); // Store URLs for each vendor

  // Process each price history entry
  product.priceHistory.forEach(item => {
    const dateKey = item.date.split('T')[0]; // Use YYYY-MM-DD as key

    if (!priceDataMap.has(dateKey)) {
      priceDataMap.set(dateKey, { date: dateKey });
    }

    const currentData = priceDataMap.get(dateKey)!;
    const vendorKey = item.retailer.toLowerCase();
    currentData[vendorKey] = item.price;
    currentData[`${vendorKey}Url`] = item.productUrl; // Store URL with vendor key
    vendors.add(vendorKey);

    // Keep track of the most recent URL for each vendor
    if (!vendorUrls.has(vendorKey)) {
      vendorUrls.set(vendorKey, item.productUrl);
    }
  });

  // Convert map to array and sort by date
  const sortedDates = Array.from(priceDataMap.keys()).sort();
  const transformedPriceData: PriceData[] = sortedDates.map(date => priceDataMap.get(date)!);

  return {
    priceData: transformedPriceData,
    vendors: Array.from(vendors),
    vendorUrls: Object.fromEntries(vendorUrls),
  };
};

/**
 * Calculate price summary (current, lowest, highest, average) for a specific retailer
 */
export const calculatePriceSummary = (
  priceHistory: PriceHistoryItem[],
  retailer: string
): PriceSummary => {
  // Filter prices for this retailer
  const prices = priceHistory
    .filter(item => item.retailer.toLowerCase() === retailer.toLowerCase())
    .map(item => item.price);

  if (prices.length === 0) {
    return {
      current: 0,
      lowest: 0,
      highest: 0,
      average: 0,
      url: undefined,
    };
  }

  // Find the most recent price entry for this retailer
  const currentPriceItem = priceHistory
    .filter(item => item.retailer.toLowerCase() === retailer.toLowerCase())
    .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime())
    .pop();

  return {
    current: currentPriceItem?.price || 0,
    lowest: Math.min(...prices),
    highest: Math.max(...prices),
    average: prices.reduce((sum, price) => sum + price, 0) / prices.length,
    url: currentPriceItem?.productUrl,
  };
};

/**
 * Get price summaries for all vendors in the product
 * Returns a map of vendor key -> vendor data with summary
 */
export const getAllVendorSummaries = (product: BabyProduct): Map<string, VendorData> => {
  const summaries = new Map<string, VendorData>();
  
  // Get unique vendors from price history
  const uniqueVendors = new Set(
    product.priceHistory.map(item => item.retailer.toLowerCase())
  );

  uniqueVendors.forEach(vendorKey => {
    const vendorName = vendorKey.charAt(0).toUpperCase() + vendorKey.slice(1);
    const summary = calculatePriceSummary(product.priceHistory, vendorKey);
    
    // Determine if this is a good deal (current price within 5% of lowest)
    const isGoodDeal = summary.current <= summary.lowest * 1.05;

    summaries.set(vendorKey, {
      name: vendorName,
      summary: summary,
      isGoodDeal: isGoodDeal,
    });
  });

  return summaries;
};

/**
 * Format date for display in chart
 */
export const formatDateForChart = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
};

/**
 * Filter price data by time range (days)
 */
export const filterByTimeRange = (priceData: PriceData[], days: number): PriceData[] => {
  if (priceData.length === 0) return [];
  
  const now = new Date();
  const cutoffDate = new Date(now.getTime() - days * 24 * 60 * 60 * 1000);
  
  return priceData.filter(item => {
    const itemDate = new Date(item.date);
    return itemDate >= cutoffDate;
  });
};

