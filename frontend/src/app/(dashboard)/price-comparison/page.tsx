'use client';

import { Suspense, useEffect, useState } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';
import { babyProductAPI } from '@/services/baby-product-api';
import type { BabyProduct, PriceHistoryItem } from '@/types/product.types';
import { useSession } from "next-auth/react";

interface ProductOffer {
  id: number; 
  name: string;
  brand: string;
  category: string;
  retailer: string;
  price: number;
}

function PriceComparisonContent() {
  const { data: session, status } = useSession();
  const searchParams = useSearchParams();
  const router = useRouter();
  const query = searchParams.get('query') || '';
  const [searchInput, setSearchInput] = useState(query);

  const [offers, setOffers] = useState<ProductOffer[]>([]);
  const [filteredOffers, setFilteredOffers] = useState<ProductOffer[]>([]);
  const [categories, setCategories] = useState<string[]>([]);
  const [retailers, setRetailers] = useState<string[]>([]);
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [selectedRetailer, setSelectedRetailer] = useState('All');
  const [sortOption, setSortOption] = useState('price-asc');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 8;   // we can change this later depending on how many items we want per page

  const latestByRetailer = (history: PriceHistoryItem[]) => {
    const retailerMap = new Map<string, PriceHistoryItem>();
    for (const historyItem of history) {
      const previousEntry = retailerMap.get(historyItem.retailer);
      if (!previousEntry || new Date(historyItem.date).getTime() > new Date(previousEntry.date).getTime()) {
        retailerMap.set(historyItem.retailer, historyItem);
      }
    }
    return [...retailerMap.values()];
  };

  const extractFilters = (data: ProductOffer[]) => {
    const uniqueCategories = Array.from(
      new Set(data.map((product) => product.category).filter((category): category is string => !!category))
    );
    const displayCategories = uniqueCategories
      .map((category) => category.trim())
      .filter(Boolean)
      .map((category) => category.charAt(0).toUpperCase() + category.slice(1));
    setCategories(['All', ...displayCategories.sort()]);

    const uniqueRetailers = Array.from(
      new Set(data.map((product) => product.retailer).filter((retailer): retailer is string => !!retailer))
    );
    const displayRetailers = uniqueRetailers
      .map((retailer) => retailer.trim())
      .filter(Boolean)
      .map((retailer) => retailer.charAt(0).toUpperCase() + retailer.slice(1));
    setRetailers(['All', ...displayRetailers.sort()]);
  };

  useEffect(() => {
    if (status === "loading") return;
    if (status === "unauthenticated") {
      router.replace("/auth/login");
    }
  }, [status, router]);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        const data: BabyProduct[] = await babyProductAPI.getAll();
        const flattened: ProductOffer[] = data.flatMap((product) => {
          if (!product.priceHistory || product.priceHistory.length === 0) return [];
          return latestByRetailer(product.priceHistory).map((historyItem) => ({
            id: product.id,
            name: product.name,
            brand: product.brand,
            category: product.category,
            retailer: historyItem.retailer,
            price: historyItem.price,
          }));
        });

        const querySearch = query.trim().toLowerCase();
        const queryFiltered = querySearch
          ? flattened.filter((offer) =>
              offer.name?.toLowerCase().includes(querySearch) ||
              offer.brand?.toLowerCase().includes(querySearch) ||
              offer.category?.toLowerCase().includes(querySearch) ||
              offer.retailer?.toLowerCase().includes(querySearch)
            )
          : flattened;

        setOffers(queryFiltered);
        extractFilters(queryFiltered);
        setError(null);
      } catch (err) {
        console.error('Failed to fetch baby products from backend', err);
        const isDevelopment = process.env.NODE_ENV === 'development';
        if (isDevelopment) {
          const errorDetails = (err as any)?.response
            ? `API Error (${(err as any).response.status}): ${(err as any).response?.data?.message || 'Server error'}`
            : (err as any)?.message || 'Network error - check if backend is running at http://localhost:8080';
          setError(`Development Error: ${errorDetails}`);
        } else {
          setError('Unable to load products at this time. Please try again later.');
        }
        setOffers([]);
        extractFilters([]);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
    setCurrentPage(1);
  }, [query]);

  useEffect(() => {
    let temp = [...offers];
    if (selectedCategory !== 'All') {
      temp = temp.filter(
        (product) => product.category?.toLowerCase() === selectedCategory.toLowerCase()
      );
    }

    if (selectedRetailer !== 'All') {
      temp = temp.filter(
        (product) => product.retailer?.toLowerCase() === selectedRetailer.toLowerCase()
      );
    }

    if (sortOption === 'price-asc') {
      temp.sort((low_price, high_price) => low_price.price - high_price.price); 
    } else if (sortOption === 'price-desc') {
      temp.sort((high_price, low_price) => low_price.price - high_price.price);
    } else if (sortOption === 'name') {
      temp.sort((lower_alphabet, upper_alphabet) => lower_alphabet.name.localeCompare(upper_alphabet.name));
    }

    setFilteredOffers(temp);
    setCurrentPage(1); 
  }, [offers, selectedCategory, selectedRetailer, sortOption]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setCurrentPage(1); 
    if (searchInput.trim()) {
      router.push(`?query=${encodeURIComponent(searchInput.trim())}`);
    } else {
      router.push('?');
    }
  };

  const handleClearSearch = () => {
    setSearchInput('');
    setCurrentPage(1); 
    router.push('?');
  };

  const resetFilters = () => {
    setSelectedCategory('All');
    setSelectedRetailer('All');
    setSortOption('price-asc');
    setCurrentPage(1);
  };

  const totalPages = Math.ceil(filteredOffers.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentPageOffers = filteredOffers.slice(startIndex, endIndex);

  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleProductClick = (productId: number) => {
    router.push(`/product/${productId}`);
  };

  if (loading) {
    return (
      <div className="p-6 min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="inline-block animate-spin rounded-full h-16 w-16 border-4 border-gray-200 border-t-[#0543BF] mb-4"></div>
          <p className="text-gray-600 font-semibold text-lg">Loading products...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="p-4 min-h-screen">
      {error && (
        <div className="mb-3 p-2 rounded border border-red-300 bg-red-50 text-red-700 text-sm">
          {error}
        </div>
      )}
      {/* Title */}
      <div className="mb-3">
        <h2 className="text-xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-[#0543BF] to-[#6F0094]">
          {query ? `Price Comparison for "${query}"` : 'All Products - Price Comparisons'}
        </h2>
      </div>

      {/* Search Bar */}
      <div className="mb-3">
        <form onSubmit={handleSearch} className="flex flex-col sm:flex-row gap-2">
          <input
            type="text"
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            placeholder="Search products by name, brand, category, or retailer..."
            className="flex-1 border border-gray-300 px-3 py-2 rounded-lg text-sm text-gray-900 bg-white focus:border-[#0543BF] focus:ring-1 focus:ring-[#0543BF]/20 transition-all outline-none placeholder:text-gray-400"
          />
          <button
            type="submit"
            className="bg-gradient-to-r from-[#0543BF] to-[#6F0094] text-white px-4 py-2 rounded-lg text-sm font-semibold hover:shadow-md transition-all"
          >
            Search
          </button>
          {query && (
            <button
              type="button"
              onClick={handleClearSearch}
              className="bg-gray-500 text-white px-4 py-2 rounded-lg text-sm font-semibold hover:bg-gray-600 transition-all"
            >
              Clear
            </button>
          )}
        </form>
      </div>

      {/* Filters and Sort */}
      <div className="flex flex-wrap gap-2 p-3 bg-gray-50 rounded-lg mb-3 border border-gray-200">
        <div className="flex flex-col flex-1 min-w-[130px]">
          <label className="text-xs font-semibold text-gray-700 mb-1 flex items-center gap-1.5">
            <span className="w-1.5 h-1.5 rounded-full bg-[#0543BF]"></span>
            Category
          </label>
          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            className="border border-gray-300 px-2 py-1.5 rounded-lg text-sm text-gray-900 bg-white focus:border-[#0543BF] focus:ring-1 focus:ring-[#0543BF]/20 transition-all outline-none cursor-pointer"
          >
            {categories.map((c) => (
              <option key={c} value={c}>
                {c}
              </option>
            ))}
          </select>
        </div>

        <div className="flex flex-col flex-1 min-w-[130px]">
          <label className="text-xs font-semibold text-gray-700 mb-1 flex items-center gap-1.5">
            <span className="w-1.5 h-1.5 rounded-full bg-[#1E88E5]"></span>
            Retailer
          </label>
          <select
            value={selectedRetailer}
            onChange={(e) => setSelectedRetailer(e.target.value)}
            className="border border-gray-300 px-2 py-1.5 rounded-lg text-sm text-gray-900 bg-white focus:border-[#0543BF] focus:ring-1 focus:ring-[#0543BF]/20 transition-all outline-none cursor-pointer"
          >
            {retailers.map((r) => (
              <option key={r} value={r}>
                {r}
              </option>
            ))}
          </select>
        </div>

        <div className="flex flex-col flex-1 min-w-[130px]">
          <label className="text-xs font-semibold text-gray-700 mb-1 flex items-center gap-1.5">
            <span className="w-1.5 h-1.5 rounded-full bg-[#6F0094]"></span>
            Sort By
          </label>
          <select
            value={sortOption}
            onChange={(e) => setSortOption(e.target.value)}
            className="border border-gray-300 px-2 py-1.5 rounded-lg text-sm text-gray-900 bg-white focus:border-[#0543BF] focus:ring-1 focus:ring-[#0543BF]/20 transition-all outline-none cursor-pointer"
          >
            <option value="price-asc">Price: Low → High</option>
            <option value="price-desc">Price: High → Low</option>
            <option value="name">Name: A → Z</option>
          </select>
        </div>

        <div className="flex flex-col justify-end">
          <button
            onClick={resetFilters}
            className="bg-gradient-to-r from-[#6F0094] to-[#0543BF] text-white px-4 py-1.5 rounded-lg text-sm font-semibold hover:shadow-md transition-all whitespace-nowrap h-fit mt-auto"
          >
            Reset Filters
          </button>
        </div>
      </div>

      {/* Results count */}
      <div className="mb-3 flex items-center justify-between">
        <p className="text-sm text-gray-700 font-medium">
          Showing <span className="text-[#0543BF] font-bold">{filteredOffers.length}</span> products
          {totalPages > 1 && ` (Page ${currentPage} of ${totalPages})`}
        </p>
      </div>

      {/* Products Grid */}
      {filteredOffers.length > 0 ? (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
            {currentPageOffers.map((product) => (
              <div
                key={`${product.id}-${product.retailer}`}
                className="group relative bg-white rounded-xl shadow-md border border-gray-200 overflow-hidden cursor-pointer transition-all duration-300 hover:shadow-2xl hover:border-[#0543BF] hover:-translate-y-1"
                onClick={() => handleProductClick(product.id)}
              >
                {/* Category badge - top section */}
                {product.category && (
                  <div className="bg-gradient-to-r from-violet-50 to-purple-50 px-4 py-2 border-b border-gray-100">
                    <span className="text-violet-700 text-xs font-bold uppercase tracking-wider">
                      {product.category}
                    </span>
                  </div>
                )}

                {/* Main content */}
                <div className="p-4">
                  {/* Product name */}
                  <h3 className="text-gray-900 font-bold text-base leading-snug line-clamp-2 mb-3 min-h-[2.5rem] group-hover:text-[#0543BF] transition-colors duration-300">
                    {product.name}
                  </h3>

                  {/* Price */}
                  <div className="mb-4">
                    <span className="text-3xl font-black text-[#0543BF] group-hover:text-[#6F0094] transition-colors duration-300">
                      ${product.price.toFixed(2)}
                    </span>
                  </div>

                  {/* Product info */}
                  <div className="space-y-2 pt-3 border-t border-gray-100">
                    {product.brand && (
                      <div className="flex items-center justify-between">
                        <span className="text-xs text-gray-500 font-medium uppercase tracking-wide">Brand</span>
                        <span className="text-sm text-gray-900 font-bold">{product.brand}</span>
                      </div>
                    )}

                    {product.retailer && (
                      <div className="flex items-center justify-between">
                        <span className="text-xs text-gray-500 font-medium uppercase tracking-wide">Retailer</span>
                        <span className="text-sm text-[#1E88E5] font-bold">{product.retailer}</span>
                      </div>
                    )}
                  </div>
                </div>

                {/* Hover overlay */}
                <div className="absolute inset-0 bg-gradient-to-t from-[#0543BF]/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none"></div>

                {/* Bottom accent line */}
                <div className="absolute bottom-0 left-0 right-0 h-1 bg-gradient-to-r from-[#0543BF] to-[#6F0094] transform scale-x-0 group-hover:scale-x-100 transition-transform duration-300"></div>
              </div>
            ))}
          </div>

          {/* Pagination Controls */}
          {totalPages > 1 && (
            <div className="flex justify-center items-center gap-3 mt-10">
              <button
                onClick={() => handlePageChange(currentPage - 1)}
                disabled={currentPage === 1}
                className={`px-5 py-2.5 rounded-lg font-semibold transition-all duration-300 border-2 ${
                  currentPage === 1
                    ? 'bg-gray-100 text-gray-400 border-gray-200 cursor-not-allowed'
                    : 'bg-white text-[#0543BF] border-[#0543BF] hover:bg-[#0543BF] hover:text-white hover:shadow-lg hover:scale-105'
                }`}
              >
                ← Previous
              </button>

              <div className="flex gap-2">
                {Array.from({ length: totalPages }, (_, index) => index + 1).map((pageNumber) => {
                  if (
                    pageNumber === 1 ||
                    pageNumber === totalPages ||
                    (pageNumber >= currentPage - 1 && pageNumber <= currentPage + 1)
                  ) {
                    return (
                      <button
                        key={pageNumber}
                        onClick={() => handlePageChange(pageNumber)}
                        className={`w-11 h-11 rounded-lg font-bold transition-all duration-300 border-2 ${
                          currentPage === pageNumber
                            ? 'bg-gradient-to-br from-[#0543BF] to-[#6F0094] text-white border-transparent shadow-lg scale-110'
                            : 'bg-white text-[#0543BF] border-gray-300 hover:border-[#0543BF] hover:shadow-md hover:scale-105'
                        }`}
                      >
                        {pageNumber}
                      </button>
                    );
                  } else if (
                    pageNumber === currentPage - 2 ||
                    pageNumber === currentPage + 2
                  ) {
                    return <span key={pageNumber} className="px-2 py-2 text-gray-500 font-bold">...</span>;
                  }
                  return null;
                })}
              </div>

              <button
                onClick={() => handlePageChange(currentPage + 1)}
                disabled={currentPage === totalPages}
                className={`px-5 py-2.5 rounded-lg font-semibold transition-all duration-300 border-2 ${
                  currentPage === totalPages
                    ? 'bg-gray-100 text-gray-400 border-gray-200 cursor-not-allowed'
                    : 'bg-white text-[#0543BF] border-[#0543BF] hover:bg-[#0543BF] hover:text-white hover:shadow-lg hover:scale-105'
                }`}
              >
                Next →
              </button>
            </div>
          )}
        </>
      ) : (
        <div className="text-center py-16">
          <div className="inline-block p-6 bg-gradient-to-br from-gray-50 to-white rounded-full mb-6 shadow-lg">
            <svg className="w-24 h-24 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
            </svg>
          </div>
          <h3 className="text-2xl font-bold text-gray-800 mb-2">
            {query ? 'No products found matching your search' : 'No products available'}
          </h3>
          <p className="text-gray-600 mb-6 max-w-md mx-auto">
            {query
              ? 'Try adjusting your search terms or clearing your filters to see more results.'
              : 'Check back later for new products and deals.'}
          </p>
          <button
            onClick={resetFilters}
            className="bg-gradient-to-r from-[#6F0094] to-[#0543BF] text-white px-8 py-3 rounded-xl font-semibold hover:shadow-lg transform hover:scale-105 transition-all duration-300"
          >
            Reset Filters
          </button>
        </div>
      )}
    </div>
  );
}

export default function PriceComparison() {
  return (
    <Suspense fallback={<div className="p-6 bg-white min-h-screen">Loading search results...</div>}>
      <PriceComparisonContent />
    </Suspense>
  );
}
