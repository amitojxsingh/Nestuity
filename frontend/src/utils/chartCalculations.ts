import { PriceData } from '@/types/product.types';

interface ChartDimensions {
  paddedMinPrice: number;
  paddedMaxPrice: number;
  paddedPriceRange: number;
}

export function calculateChartDimensions(
  priceData: PriceData[],
  vendors: string[]
): ChartDimensions {
  // Collect all prices from the data
  const allPrices: number[] = [];
  priceData.forEach((d) => {
    vendors.forEach((vendor) => {
      const price = d[vendor];
      if (typeof price === 'number') {
        allPrices.push(price);
      }
    });
  });

  // Calculate min and max prices
  const maxPrice = allPrices.length > 0 ? Math.max(...allPrices) : 100;
  const minPrice = allPrices.length > 0 ? Math.min(...allPrices) : 0;
  const priceRange = maxPrice - minPrice;

  // Add padding to the price range for better visualization
  const paddedMinPrice = minPrice - priceRange * 0.1;
  const paddedMaxPrice = maxPrice + priceRange * 0.1;
  const paddedPriceRange = paddedMaxPrice - paddedMinPrice;

  return {
    paddedMinPrice,
    paddedMaxPrice,
    paddedPriceRange,
  };
}

