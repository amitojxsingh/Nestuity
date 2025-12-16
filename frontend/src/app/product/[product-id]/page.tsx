'use client';

import { useState, useEffect, use } from 'react';
import { BabyProduct, PriceData, TimeRange, VendorData } from '@/types/product.types';
import { babyProductAPI } from '@/services/baby-product-api';
import { transformPriceHistory, getAllVendorSummaries } from '@/utils/priceDataHelper';
import { calculateChartDimensions } from '@/utils/chartCalculations';

// Components
import LoadingState from '../components/LoadingState';
import ErrorState from '../components/ErrorState';
import TimeRangeFilter from '../components/TimeRangeFilter';
import VendorFilter from '../components/VendorFilter';
import PriceChart from '../components/PriceChart';
import PriceSummaryTable from '../components/PriceSummaryTable';
import ProductDetailsCard from '../components/ProductDetailsCard';
import Navbar from '@/components/Header/Navbar';
import { useRouter } from 'next/navigation';
import { useSession } from "next-auth/react";

interface ProductPageProps {
  params: Promise<{
    'product-id': string;
  }>;
}

export default function ProductPage({ params }: ProductPageProps) {
  const { data: session, status } = useSession();
  const router = useRouter();
  const resolvedParams = use(params);
  const productId = resolvedParams['product-id'];

  // State management
  const [selectedTimeRange, setSelectedTimeRange] = useState('1 Month');
  const [selectedVendor, setSelectedVendor] = useState('All');
  const [product, setProduct] = useState<BabyProduct | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [priceData, setPriceData] = useState<PriceData[]>([]);
  const [vendors, setVendors] = useState<string[]>([]);
  const [vendorSummaries, setVendorSummaries] = useState<Map<string, VendorData>>(new Map());

  // Time range options
  const timeRanges: TimeRange[] = [
    { label: '1 Month', value: '1M', days: 30 },
    { label: '3 Months', value: '3M', days: 90 },
    { label: '6 Months', value: '6M', days: 180 },
  ];

  useEffect(() => {
    if (status === "loading") return;
    if (status === "unauthenticated") {
      router.replace("/auth/login");
    }
  }, [status, router]);

  // Fetch product data from API
  useEffect(() => {
    const fetchProduct = async () => {
      try {
        setLoading(true);
        const data = await babyProductAPI.getById(Number(productId));
        setProduct(data);

        // Transform data for chart
        const { priceData: transformedData, vendors: vendorsList } = transformPriceHistory(data);
        setPriceData(transformedData);
        setVendors(vendorsList);

        // Calculate vendor summaries
        const summaries = getAllVendorSummaries(data);
        setVendorSummaries(summaries);

        setError(null);
      } catch (err) {
        console.error('Error fetching product:', err);
        setError('Failed to load product data. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [productId]);

  // Calculate chart dimensions
  const { paddedMinPrice, paddedMaxPrice, paddedPriceRange } = calculateChartDimensions(
    priceData,
    vendors
  );

  // Loading state
  if (loading) {
    return <LoadingState />;
  }

  // Error state
  if (error || !product) {
    return <ErrorState message={error || 'Product not found'} />;
  }

  // No price data available
  if (priceData.length === 0) {
    return (
      <div className="min-h-screen bg-white flex items-center justify-center">
        <div className="text-center p-4">
          <h2 className="text-xl font-semibold text-gray-900 mb-2">{product.name}</h2>
          <p className="text-gray-600">No price history available for this product yet.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen h-full flex flex-col bg-primary">
      {/* Header with navigation */}
      <Navbar />

      {/* Product Details Card and Vendor Filter - Side by Side */}
      <div className="px-4 mb-3 pt-30">
        <div className="flex flex-col md:flex-row gap-3">
          {/* Product Details Card */}
          <div className="md:w-1/2">
            <ProductDetailsCard product={product} />
          </div>

          {/* Vendor Filter */}
          <div className="md:w-1/2 flex items-center">
            <div className="w-full">
              <label className="text-sm font-semibold text-dark-grey mb-2 block">Filter by Vendor</label>
              <VendorFilter
                vendors={vendors}
                selectedVendor={selectedVendor}
                onSelectVendor={setSelectedVendor}
              />
            </div>
          </div>
        </div>
      </div>

      {/* Price Summary Table */}
      <div className="px-4 mb-8">
        <PriceSummaryTable vendorSummaries={vendorSummaries} />
      </div>

      {/* Price Chart */}
      <div className="px-4 mb-4">
        <PriceChart
          priceData={priceData}
          vendors={vendors}
          selectedVendor={selectedVendor}
          paddedMinPrice={paddedMinPrice}
          paddedMaxPrice={paddedMaxPrice}
          paddedPriceRange={paddedPriceRange}
        />
      </div>
    </div>
  );
}
